package com.hansung.capstone

import android.app.Application
import android.content.Context
import com.kakao.sdk.common.KakaoSdk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class MyApplication : Application() {

    init {
        instance = this
    }

    companion object {
        lateinit var prefs: Preference

        // 서버 주소
        private const val serverInfo = "3.39.20.68:8080"
        private const val url = "http://$serverInfo/"
        fun getUrl(): String {
            return url
        }

        // context 접근용
        lateinit var instance: MyApplication
        fun applicationContext(): Context {
            return instance.applicationContext
        }

        // LocalDateTime 변환
        fun convertDate(date: String): LocalDateTime {
            val subDate = date.substring(0 until minOf(date.length, 19))
            val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            return LocalDateTime.parse(subDate, pattern)
        }
        fun checkUpdateTime():Boolean{
            val calendar = Calendar.getInstance()
            val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

            return currentHour == 7
        }
    }

    override fun onCreate() {
        prefs = Preference(applicationContext)
        super.onCreate()
        KakaoSdk.init(this, "be2fdfcd5beaf4a4ecf3930746b2bf69")
    }

}