package com.hansung.capstone

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
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

typealias coordinates = MutableList<LatLng>
typealias paths = MutableList<coordinates>

class RidingService : LifecycleService() {
    private var ridingTime = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient // 사용자 위치를 따오기 위해

    companion object {
        val ridingLocation = MutableLiveData<LatLng>()
        val pathOverlay = MutableLiveData<paths>()
        val ridingTimer = MutableLiveData<Int>() // 뷰에 표시될 시간
        val isRiding = MutableLiveData<Boolean>() // 기록중인지 판별
    }

    //  Service 객체가 처음 생성될 때 호출되는 함수, Service 초기화 작업 수행
    override fun onCreate() {
        super.onCreate()
        initServiceLiveDataValues() // LiveData 초기값 설정

        // 사용자 좌표를 얻어오기 위한 FusedLocationProviderClient 객체 생성
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(MyApplication.applicationContext())
        isRiding.observe(this) {
            updateLocationChecking(it)
        }
    }

    // 서비스가 시작되면 호출되는 함수, 액션에 따라 다른 처리
    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {
                    Log.d("test", "서비스 시작")
                    Log.d("test", "서비스중..")
                    startRidingForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    Log.d("test", "서비스 중지")
                    stopRidingForegroundService()
                }
                else -> Log.d("", "else")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    // Service 소멸될 때 호출되는 함수, 필요한 자원을 해제하고 정리하는 작업을 수행
    override fun onDestroy() {
        Log.d("Service", "서비스 종료")
        super.onDestroy()
    }

    // 포그라운드 서비스 시작하는 함수
    // 타이머 시작, isRiding = true 변경, 포그라운드 서비스 시작, 알림에 나올 시간 갱신
    private fun startRidingForegroundService() {
        isRiding.postValue(true) // 기록 상태 true
        startRidingTimer()
        addEmptyPolyline()

        // notificationManager 생성
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager) // 알림 채널 만들고

        // 만든 채널에 알림 등록, 클릭 시 라이딩 화면 복귀
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Riding..")
            .setContentText("00:00")
            .setContentIntent(moveRidingActivityPendingIntent())

        // 포그라운드 서비스 시작
        startForeground(NOTIFICATION_ID, notificationBuilder.build())

    }

    // 서비스 LiveData 초기값 설정 함수
    private fun initServiceLiveDataValues() {
        isRiding.postValue(false)
//        ridingTimer.postValue(0)
        pathOverlay.postValue(mutableListOf())
    }

    // location 객체로부터 사용자 위도, 경도 얻어오기
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            if (isRiding.value!!) {
                locationResult.locations.let {
//                    Log.d("locationResult",it.toString())
                    // locationResult 위도, 경도 읽어서 좌표 LiveData 값 변경
                    for (location in it) {
                        val locationUpdate = LatLng(location.latitude, location.longitude)
//                        Log.d("좌표가찍히냐?콜백", "${location.latitude} ${location.longitude}")
//                        ridingLocation.postValue(locationUpdate)
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun moveRidingActivityPendingIntent() = getActivity(
        this,
        0,
        Intent(this, RidingActivity::class.java),
        FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
    )

    private fun stopRidingForegroundService() {
        isRiding.postValue(false)
        initServiceLiveDataValues()
//        stopForeground(true)
        stopSelf()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationChecking(isTracking: Boolean) {
        if (isTracking) {
            if (ActivityCompat.checkSelfPermission(
                    MyApplication.applicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    MyApplication.applicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            val locationRequest = LocationRequest.create().apply {
                interval = 10000L
                fastestInterval = 2000L
                priority = PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    // 알림 채널 생성 함수
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }

    // 시간 기록 시작하는 함수
    private fun startRidingTimer() {
        CoroutineScope(Dispatchers.Main).launch {
            // 라이딩 중이면 시간초를 센다
            while (isRiding.value!!) {
                ridingTime += 1
                ridingTimer.postValue(ridingTime)
                delay(1000)
            }
        }
    }

    object Riding {
        fun convertStopwatch(total: Int): String {
            val minute = String.format("%02d", total / 60)
            val second = String.format("%02d", total % 60)
            return "$minute:$second"
        }
    }

    private fun addEmptyPolyline() = pathOverlay.value?.apply {
        add(mutableListOf())
        pathOverlay.postValue(this)
    } ?: pathOverlay.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?) {
        location?.let {
            val lastCoordinate = LatLng(it.latitude, it.longitude)
            Log.d("좌표가찍히냐?addPathPoint", "${it.latitude} ${it.longitude}")
            pathOverlay.value?.apply {
                if (lastOrNull() == null) {
                    add(mutableListOf(lastCoordinate))
                } else {
                    last().add(lastCoordinate)
                }
                pathOverlay.postValue(this)
            }
        }
    }
}