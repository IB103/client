package com.hansung.capstone

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.databinding.ActivityLoginBinding
import com.hansung.capstone.retrofit.RepLogin
import com.hansung.capstone.retrofit.ReqLogin
import com.hansung.capstone.retrofit.RetrofitService
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {
    var kakao_email: String? = null
    var kakao_nickname: String? = null
    var manager: FragmentManager = supportFragmentManager
    val gson: Gson = GsonBuilder().setLenient().create()
    var server_info = MyApplication.getUrl() //username password1 password2 email
    var retrofit = Retrofit.Builder().baseUrl("$server_info")
        .addConverterFactory(GsonConverterFactory.create(gson)).build()
    var service = retrofit.create(RetrofitService::class.java)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()

            } else if (tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                Log.d("1", "${tokenInfo}")
                updateKakaoLoginInfo()
                val intent = Intent(this, MyPageFragment::class.java)
                setResult(RESULT_OK, intent)
            }
        }
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT)
                            .show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                //  val intent = Intent(this, SecondActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }
        binding.loginbt.setOnClickListener {
            val email = binding.idtext.text.toString()
            val pw = binding.pwtext.text.toString()
            Log.d("Email", email)
            Log.d("Pw", pw)
            //////////////////////////////
            var postReqLogin = ReqLogin(email, pw)
            service.login(postReqLogin).enqueue(object : Callback<RepLogin> {
                @SuppressLint("Range")
                override fun onResponse(call: Call<RepLogin>, response: Response<RepLogin>) {
                    if (response.isSuccessful) {
                        Log.d("req", "OK")
                        var result: RepLogin? = response.body()
                        if (response.code() == 200) {//수정해야함
                            if (result?.code == 100) {
                                Log.d("로그인", "성공:" + result)
                                Log.d("로그인", "닉네임" + result.data.nickname)
                                // Toast.makeText(this, "로그인이 완료됐습니다.", Toast.LENGTH_SHORT).show()
                                MyApplication.prefs.setString("email", email)
                                MyApplication.prefs.setString("nickname", result.data.nickname)
                                MyApplication.prefs.setString("accesstoken", result.data.tokenInfo.accessToken)
                                MyApplication.prefs.setInt("profileImageId", result.data.profileImageId)
                                MyApplication.prefs.setInt("userId",result.data.userId)
                                test()
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                Log.d("ERR", "실패: " + result?.toString())
                            }
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("ERR", "onResponse 실패")
                        MyApplication.prefs.setString("Login", "fail")

                    }
                }

                override fun onFailure(call: Call<RepLogin>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("ERR", "onFailure 에러: " + t.message.toString())
                    MyApplication.prefs.setString("Login", "fail")
                }
            })
        }
        binding.kakaologinBt.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    private fun updateKakaoLoginInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
            } else if (user != null) {
                kakao_email = "${user.kakaoAccount?.email}"
                MyApplication.prefs.setString("id", "${kakao_email}")
                Log.d("kakaoemail:", MyApplication.prefs.getString("id", ""))
                kakao_nickname = user.kakaoAccount?.profile?.nickname
                MyApplication.prefs.setString("nickname", "${kakao_nickname}")
                MyApplication.prefs.setString("state", "kakao")
                val intent = Intent(this, MyPageFragment::class.java)
                setResult(RESULT_OK, intent)
                finish()
                Log.i(
                    ContentValues.TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: ${user.id}" +
                            "\n이메일: ${user.kakaoAccount?.email}" +
                            "\n닉네임: ${user.kakaoAccount?.profile?.nickname}" +
                            "\n프로필사진: ${user.kakaoAccount?.profile?.thumbnailImageUrl}"
                )
            }
        }

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null && ev != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm?.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun test() {
        service.test(accessToken = "Bearer ${MyApplication.prefs.getString("accesstoken","")}").enqueue(object : Callback<String> {
            @SuppressLint("Range")
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    Log.d("test", "OK")
                    var result:String = response.body().toString()
                    if (response.code() == 200) {//수정해야함
                        Log.d("test", "성공:" + result)
                    }
                } else {
                    // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                    Log.d("ERR", "onResponse test 실패")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("ERR", "onFailure 에러: " + t.message.toString())
            }
        })
    }

}