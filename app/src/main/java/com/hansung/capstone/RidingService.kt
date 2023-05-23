package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.hansung.capstone.Constants.ACTION_CREATE_SERVICE
import com.hansung.capstone.Constants.ACTION_PAUSE_SERVICE
import com.hansung.capstone.Constants.ACTION_START_OR_RESUME_SERVICE
import com.hansung.capstone.Constants.ACTION_STOP_SERVICE
import com.hansung.capstone.Constants.NOTIFICATION_CHANNEL_ID
import com.hansung.capstone.Constants.NOTIFICATION_CHANNEL_NAME
import com.hansung.capstone.Constants.NOTIFICATION_ID
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round

typealias line = MutableList<LatLng> // 좌표가 담긴 리스트(선)
typealias waypoints = MutableList<Waypoint> // 좌표가 담긴 리스트(선)

//const val INTERVAL = 10000 // 10m
//const val INTERVAL = 100000 // 1m
const val INTERVAL = 1000000 // 0.1m

class RidingService : LifecycleService() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // 사용자 위치를 따오기 위해
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L
    private var totalTime: Long = 0L // 전체시간
    private var lastSecondTimestamp = 0L // 1초 단위 체크를 위함
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager

    companion object {
        val currentLocation = MutableLiveData<LatLng>()
        val pathOverlay = MutableLiveData<line>()
        val pathWaypoints = MutableLiveData<waypoints>()
        val isRiding = MutableLiveData<Boolean>() // 기록중인지 판별
        val timeLiveData = MutableLiveData<Long>(0) // 뷰에 표시될 시간
        val distanceLiveData = MutableLiveData<Float>() // 거리
        val speedLiveData = MutableLiveData<Float>() // 속도
        val kcalLiveData = MutableLiveData<Int>() // 칼로리
//        val distanceLiveData = MediatorLiveData<Float>() // 거리
//        val speedLiveData = MediatorLiveData<Float>() // 속도
//            .apply {
//                addSource(timeLiveData) {
//                    val distance = distanceLiveData.value
//                    if (distance != null && distance != 0f) {
//                        this.postValue(RidingUtility.calculateSpeed(it, distance))
//                    }
//                }
//            }
//        val kcalLiveData = MediatorLiveData<Int>() // 칼로리
//            .apply {
//                addSource(timeLiveData) {
//                    val speed = speedLiveData.value
//                    if (speed != null && speed != 0f) {
//                        this.postValue(RidingUtility.calculateKcal(it, speed).toInt())
//                    }
//                }
//            }
    }

    //  Service 객체가 처음 생성될 때 호출되는 함수, Service 초기화 작업 수행
    override fun onCreate() {
        super.onCreate()
        Log.d("RidingService", "onCreate")

        // notificationManager 생성
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager) // 알림 채널 만들기

        initNotificationBuilder() // 알림 빌더 만들기
        initServiceLiveDataValues() // LiveData 초기값 설정

        // 사용자 좌표를 얻어오기 위한 FusedLocationProviderClient 객체 생성
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
//        isRiding.observe(this) {
//            updateLocationChecking(it)
//        }
        updateLocationChecking()
    }

    // 서비스가 시작되면 호출되는 함수, 액션에 따라 다른 처리
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_CREATE_SERVICE -> {
                    Log.d("RidingServiceAction", "서비스 생성")
                }
                ACTION_START_OR_RESUME_SERVICE -> {
                    Log.d("RidingServiceAction", "포그라운드 서비스 시작")
                    startRidingForegroundService()
                }
                ACTION_PAUSE_SERVICE -> {
                    Log.d("RidingServiceAction", "타이머, 경로 기록 중지")
                    isRiding.postValue(false)
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("RidingServiceAction", "포그라운드 서비스, 서비스 종료")
                    stopRidingForegroundService()
                }
                else -> Log.d("RidingServiceAction", it.action.toString())
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Service 소멸될 때 호출되는 함수, 필요한 자원을 해제하고 정리하는 작업을 수행
    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        notificationManager.cancel(NOTIFICATION_ID)
        Log.d("RidingService", "onDestroy")
        super.onDestroy()

    }

    // 포그라운드 서비스 시작하는 함수
    // 타이머 시작, isRiding = true 변경, 포그라운드 서비스 시작, 알림에 나올 시간 갱신
    private fun startRidingForegroundService() {
        isRiding.postValue(true) // 기록 상태 true
        startTimer()

        // 포그라운드 서비스 시작
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

        timeLiveData.observe(this) {
            notificationBuilder.setContentText(RidingUtility.convertMs(it))
            notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    // 기록 끝
    private fun stopRidingForegroundService() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        initServiceLiveDataValues()
        stopSelf()
    }

    // 서비스 LiveData 초기값 설정 함수
    private fun initServiceLiveDataValues() {
        isRiding.postValue(false)
        timeLiveData.postValue(0L)
        distanceLiveData.postValue(0f)
        speedLiveData.postValue(0f)
        kcalLiveData.postValue(0)
        pathOverlay.postValue(mutableListOf())
    }

    private fun initNotificationBuilder() {
        notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("자전GO")
            .setContentText("00:00:00") // 시간 출력 위치
            .setContentIntent(backToRidingActivity()) // 알림 클릭 시 액티비티로 복귀 동작할 PendingIntent
    }

    // location 객체로부터 사용자 위도, 경도 얻어오기
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.let {
                // locationResult 위도, 경도 읽어서 좌표 LiveData 값 변경
                for (location in it) {
                    val lastLocation = LatLng(
                        round(location.latitude * INTERVAL) / INTERVAL,
                        round(location.longitude * INTERVAL) / INTERVAL
                    )
                    // 현 위치를 갱신
                    currentLocation.postValue(lastLocation)
                    addLastLocation(lastLocation) // location 객체를 경로에 추가
                }
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun backToRidingActivity() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, RidingActivity::class.java),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    @SuppressLint("MissingPermission")
    private fun updateLocationChecking() {
        // 위치 요청 값 설정
        val locationRequest = LocationRequest.create().apply {
            interval = 1000L
            fastestInterval = 500L
            priority = PRIORITY_HIGH_ACCURACY
        }
        // 갱신되는 위치 확인
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    // 알림 채널 생성 함수
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    private fun addLastLocation(lastLocation: LatLng) {
        lastLocation.let {
            pathOverlay.value?.apply {
                if (isRiding.value!!) {
                    // 리스트가 비어있지 않고 마지막 좌표와 새로 얻은 좌표가 다를 때만 추가
                    if (this.isNotEmpty()) {
                        if (this.last() != lastLocation) {
                            add(lastLocation)
                            pathOverlay.postValue(this)
                        }
                    } else { // 리스트가 비었을 땐 그냥 추가
                        add(lastLocation)
                        pathOverlay.postValue(this)
                    }
                }
                Log.d("RidingService", "LiveData pathOverlay: " + pathOverlay.value!!.toString())
            }
        }
    }

    private fun startTimer() {
        startTime = System.currentTimeMillis()
        CoroutineScope(Dispatchers.Main).launch {
            while (isRiding.value!!) {
                elapsedTime = System.currentTimeMillis() - startTime
                timeLiveData.postValue(totalTime + elapsedTime)
                if (timeLiveData.value!! >= lastSecondTimestamp + 1000L) {
                    timeLiveData.postValue(timeLiveData.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(100L)
            }
            totalTime += elapsedTime
        }
    }
}