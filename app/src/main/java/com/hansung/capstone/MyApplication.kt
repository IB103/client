package com.hansung.capstone

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MyApplication : Application() {
    companion object {
        lateinit var prefs: Preference

        // 서버 주소
//        private const val serverInfo = "121.138.93.178:9999"
        private const val serverInfo = "223.194.133.220:8080"
//        private const val serverInfo = "14.52.209.63:8080"
        private const val url = "http://$serverInfo/"
        fun getUrl(): String {
            return url
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