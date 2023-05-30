package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.databinding.ActivityLoginBinding
import com.hansung.capstone.find.FindIdActivity
import com.hansung.capstone.find.FindPwActivity
import com.hansung.capstone.mypage.MyPageFragment
import com.hansung.capstone.retrofit.RepLogin
import com.hansung.capstone.retrofit.ReqLogin
import com.hansung.capstone.retrofit.RetrofitService
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {
    var token=Token()
    private var kakaoEmail: String? = null
    private var kakaoNickname: String? = null
    var service = RetrofitService.create()
    private var loginNeeded:Boolean=false

    companion object {
        private const val MODIFYPWACT_REQUEST_CODE = 12
        private const val SIGNUP_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginNeeded = intent.getBooleanExtra("loginNeeded", false)
        if(loginNeeded){
            Toast.makeText(this, "로그인이 필요한 활동입니다", Toast.LENGTH_SHORT).show()
        }

        binding.findId.setOnClickListener {
            val intent = Intent(this, FindIdActivity::class.java)
            startActivity(intent)
        }
        binding.findPw.setOnClickListener {
            val intent = Intent(this, FindPwActivity::class.java)
            startActivityForResult(intent, MODIFYPWACT_REQUEST_CODE)
        }
        binding.loginbt.setOnClickListener {
            val email = binding.idtext.text.toString()
            val pw = binding.pwtext.text.toString()
            Log.d("Email", email)
            Log.d("Pw", pw)
            //////////////////////////////
            val postReqLogin = ReqLogin(email, pw)
            service.login(postReqLogin).enqueue(object : Callback<RepLogin> {
                @SuppressLint("Range")
                override fun onResponse(call: Call<RepLogin>, response: Response<RepLogin>) {
                    if (response.isSuccessful) {
                        Log.d("req", "OK")
                        val result: RepLogin? = response.body()
                        if (response.code() == 200) {
                            if (result?.code == 100) {
                                Log.d("로그인", "성공:$result")
                                Log.d("로그인", "닉네임" + result.data.nickname)
                                MyApplication.prefs.setString("email", email)
                                MyApplication.prefs.setString("nickname", result.data.nickname)
                                MyApplication.prefs.setString(
                                    "accessToken",
                                    result.data.tokenInfo.accessToken
                                )
                                MyApplication.prefs.setString(
                                    "refreshToken",
                                    result.data.tokenInfo.refreshToken
                                )
                                MyApplication.prefs.setString("username", result.data.username)
                                MyApplication.prefs.setLong(
                                    "profileImageId",
                                    result.data.profileImageId
                                )
                                MyApplication.prefs.setLong("userId", result.data.userId)
                                token.set()
                                setResult(RESULT_OK)
                                MainActivity.getInstance()!!.setLoginState(1)
                                finish()
                            }
                        }
                    } else {
                        Log.d("ERR", "onResponse 실패")
                        Toast.makeText(this@LoginActivity,"아이디 또는 비밀번호를\n 다시 확인해주세요.",Toast.LENGTH_SHORT).show()
                        //MyApplication.prefs.setString("Login", "fail")

                    }
                }

                override fun onFailure(call: Call<RepLogin>, t: Throwable) {
                    Log.d("ERR", "onFailure 에러: " + t.message.toString())
                    MyApplication.prefs.setString("Login", "fail")
                }
            })
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivityForResult(intent, SIGNUP_REQUEST_CODE)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGNUP_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
           // completeSignUp = data?.getBooleanExtra("key", false) ?: false
            Toast.makeText(this,"회원가입이 완료됐습니다. 로그인 해주세요.",Toast.LENGTH_SHORT).show()
            // completeSignUp 값 활용
        }
        else if (requestCode ==MODIFYPWACT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this,"비밀번호 변경이 완료됐습니다.", Toast.LENGTH_SHORT).show()
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
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


}