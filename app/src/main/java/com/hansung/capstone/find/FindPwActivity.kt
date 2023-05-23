package com.hansung.capstone.find

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityFindpwBinding
import com.hansung.capstone.modify.ModifyActivity
import com.hansung.capstone.retrofit.RepConfirm
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPwActivity:AppCompatActivity() {
    val service=RetrofitService.create()
    private val binding by lazy {ActivityFindpwBinding.inflate(layoutInflater) }
    private  val MODIFYPW_REQUEST_CODE = 12
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.closeView.setOnClickListener { finish() }
        binding.reqEmailSend.setOnClickListener {
            val email=binding.getId.text.toString()
            service.send(email).enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        setTime()
                        binding.confirmValue.isEnabled=true

                    } else {
                        val errorBody = response.errorBody()

                        println("Failed to send email. Error: $errorBody")
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    // Handle the network request failure here
                    println("Failed to send email. Exception: ${t.message}")
                }
            })



//            service.send(email).enqueue(object : Callback<String> {
//                @SuppressLint("Range", "ResourceAsColor")
//                override fun onResponse(
//                    call: Call<String>,
//                    response: Response<String>
//                ) {
//                    if (response.isSuccessful) {
//                       // if (response.code() == 200) {
//
//                                Log.d("INFO", "코드 받기 성공")
//                                setTime()
//                                binding.confirmValue.isEnabled=true
//                          //  }
//                          //  }
//                    else {
//                            Log.d("ERR", "코드 받기 실패")
//                        }
//                    } else {
//                        // 통신이 실패한 경우
//                        Log.d("ERR 코드 받기", "onResponse 실패")
//                    }
//                }
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d("ERR 코드 받기", "onFailure 에러: " + t.message.toString())
//
//                }
//            })
        }

    }
    private var timer: CountDownTimer? = null // 타이머 객체를 클래스 변수로 선언
    private fun setTime() {
        Toast.makeText(this, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
        timer = object : CountDownTimer(300000, 1000) { // 5분
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / (1000 * 60)).toInt()
                val seconds = (millisUntilFinished / 1000 % 60).toInt()
                binding.count.text = "$minutes:$seconds"
            }
            override fun onFinish() {
                comment()
            }
        }
        timer?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
    private fun comment(){ Toast.makeText(this,"인증번호를 재발급 받으세요",Toast.LENGTH_SHORT).show()}
    fun reqConfirm(view: View){
        val email=binding.getId.text.toString()
        val value=binding.getValue.text.toString()
        service.confirm(email,value).enqueue(object : Callback<RepConfirm> {
            @SuppressLint("Range", "ResourceAsColor", "SuspiciousIndentation")
            override fun onResponse(
                call: Call<RepConfirm>,
                response: Response<RepConfirm>,
            ) {
                if (response.isSuccessful) {
                    val result: RepConfirm = response.body()!!
                        if (response.code() == 200) {//수정
                            Log.d("INFO", "인증 성공$result")
                            MyApplication.prefs.setString("accessToken","${result.data.accessToken}")
                            MyApplication.prefs.setString("refreshToken","${result.data.refreshToken}")
                            startActivity()
                        }
                } else {
                    // 통신이 실패한 경우
                    Log.d("ERR 인증 받기", "onResponse 실패")
                }
            }
            override fun onFailure(call: Call<RepConfirm>, t: Throwable) {
                Log.d("ERR 인증", "onFailure 에러: " + t.message.toString())

            }
        })
    }
    private fun startActivity(){
        val intent = Intent(this, ModifyActivity::class.java)
        startActivityForResult(intent,MODIFYPW_REQUEST_CODE)

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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MODIFYPW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }
}