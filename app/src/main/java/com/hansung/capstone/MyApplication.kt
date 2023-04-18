package com.hansung.capstone

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyApplication : Application() {

//    val ridingViewModel by lazy {
//        ViewModelProvider.AndroidViewModelFactory.getInstance(this).create(RidingViewModel::class.java)
//    }

    init{
        instance = this
    }

    companion object {
        lateinit var prefs: Preference

        // 서버 주소
        //private const val serverInfo = "52.63.152.224:8080"
        private const val serverInfo = "223.194.130.55:8080"
        //private const val serverInfo = "172.:8080"
        //private const val serverInfo = "23.63.152.224:8080"
     //  private const val serverInfo = "223.194.129.216:8080"
       //private const val serverInfo = "14.52.209.166:8080"
       // private const val serverInfo = "192.168.219.108:8080"
       // private const val serverInfo = "121.138.93.178:9999"
//        private const val serverInfo = "223.194.129.170:8080"
//        private const val serverInfo = "52.63.152.224:8080"
//        private const val serverInfo = "14.52.209.63:8080"

        private const val url = "http://$serverInfo/"
        fun getUrl(): String {
            return url
        }
        // context 접근용
        lateinit var instance: MyApplication
        fun applicationContext() : Context {
            return instance.applicationContext
        }
        // LocalDateTime 변환
        fun convertDate(date: String): LocalDateTime {
            val subDate = date.substring(0 until 19)
            val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            return LocalDateTime.parse(subDate, pattern)
        }
    }

    override fun onCreate() {
        prefs = Preference(applicationContext)
        super.onCreate()
        KakaoSdk.init(this, "be2fdfcd5beaf4a4ecf3930746b2bf69")
    }

}