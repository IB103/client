package com.hansung.capstone

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    companion object{
        lateinit var prefs: Preference
    }

    override fun onCreate() {
        prefs = Preference(applicationContext)
        super.onCreate()
        KakaoSdk.init(this, "be2fdfcd5beaf4a4ecf3930746b2bf69")
    }
}