package com.hansung.capstone

import android.annotation.SuppressLint
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
import com.hansung.capstone.databinding.ActivityFindpwBinding
import com.hansung.capstone.modify.ModifyActivity
import com.hansung.capstone.retrofit.RepConfirm
import com.hansung.capstone.retrofit.RepSend
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPwActivity:AppCompatActivity() {
    val service=RetrofitService.create()
    private val binding by lazy {ActivityFindpwBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.closeView.setOnClickListener { finish() }
        setTime()
        binding.reqEmailSend.setOnClickListener {
            val email=binding.getId.text.toString()
            service.send(email).enqueue(object : Callback<RepSend> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepSend>,
                    response: Response<RepSend>,
                ) {
                    if (response.isSuccessful) {
                        val result: RepSend = response.body()!!
                        if (response.code() == 200) {
                            if (result.code == 200) {//수정
                                Log.d("INFO", "코드 받기 성공 $result")
                                setTime()
                                binding.confirmValue.isEnabled=true
                            } else {
                                Log.d("ERR", "코드 받기 실패: $result")
                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("ERR 코드 받기", "onResponse 실패")
                    }
                }
                override fun onFailure(call: Call<RepSend>, t: Throwable) {
                    Log.d("ERR 코드 받기", "onFailure 에러: " + t.message.toString())

                }
            })
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
                        if (result.code == 200) {//수정
                            Log.d("INFO", "인증 성공$result")
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
        startActivity(intent)
        finish()
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