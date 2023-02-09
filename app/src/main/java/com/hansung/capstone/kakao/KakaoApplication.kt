package com.hansung.capstone.kakao

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class KakaoApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화
        KakaoSdk.init(this, "be2fdfcd5beaf4a4ecf3930746b2bf69")
    }
}