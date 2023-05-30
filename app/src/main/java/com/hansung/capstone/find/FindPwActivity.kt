@file:Suppress("DEPRECATION")

package com.hansung.capstone.find

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityFindpwBinding
import com.hansung.capstone.modify.ModifyActivity
import com.hansung.capstone.retrofit.RepConfirm
import com.hansung.capstone.retrofit.RepUser
import com.hansung.capstone.retrofit.RetrofitService
import kotlinx.android.synthetic.main.activity_findpw.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindPwActivity:AppCompatActivity() {
    val service=RetrofitService.create()
    private val binding by lazy {ActivityFindpwBinding.inflate(layoutInflater) }
    private  val MODIFY_PW_REQUEST_CODE = 12
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if(MyApplication.prefs.getLong("userId",0L)!=0L){
            val email=MyApplication.prefs.getString("email","")
            binding.getId.setText(email)
        }
        binding.confirmValue.setOnClickListener {
            confirmValue()
        }
        setSupportActionBar(binding.CommunityToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        binding.closeView.setOnClickListener { finish() }
        binding.reqEmailSend.setOnClickListener {
        binding.reqEmailSend.isEnabled=false
             sendEmail()
        }

    }
    private fun sendEmail(){
        val email=binding.getId.text.toString()
        service.send(email).enqueue(object : Callback<RepUser> {
            override fun onResponse(call: Call<RepUser>, response: Response<RepUser>) {
                if (response.isSuccessful) {
                    val result = response.body()!!
                    if(result.code==100){
                    binding.reqEmailSend.isEnabled=true
                    setTime()
                    binding.confirmValue.isEnabled=true
                    }
                } else {
                    val errorBody = response.errorBody()

                    println("Failed to send email. Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<RepUser>, t: Throwable) {
                // Handle the network request failure here
                println("Failed to send email. Exception: ${t.message}")
            }
        })
    }
    private var timer: CountDownTimer? = null // 타이머 객체를 클래스 변수로 선언
    private fun setTime() {
        Toast.makeText(this, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show()
        timer = object : CountDownTimer(300000, 1000) { // 5분
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / (1000 * 60)).toInt()
                val seconds = (millisUntilFinished / 1000 % 60).toInt()
                if(seconds<10)
                    binding.count.text = "$minutes:0$seconds"
                else
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

    override fun onBackPressed() {
        super.onBackPressed()
        timer?.cancel()

    }
    private fun comment(){ Toast.makeText(this,"인증번호를 재발급 받으세요",Toast.LENGTH_SHORT).show()}
    private fun confirmValue(){
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
                        MyApplication.prefs.setString("accessToken", result.data.accessToken)
                        MyApplication.prefs.setString("refreshToken", result.data.refreshToken)
                        startActivity()
                    }
                } else {
                    // 통신이 실패한 경우
                    mismatchComment()
                    Log.d("ERR 인증 받기", "onResponse 실패")
                }
            }
            override fun onFailure(call: Call<RepConfirm>, t: Throwable) {
                Log.d("ERR 인증", "onFailure 에러: " + t.message.toString())

            }
        })
    }
    private fun mismatchComment(){
        Toast.makeText(this,"인증번호를 다시 확인해주세요.",Toast.LENGTH_SHORT).show()
    }

    private fun startActivity(){
        timer?.cancel()
        val intent = Intent(this, ModifyActivity::class.java)
        startActivityForResult(intent,MODIFY_PW_REQUEST_CODE)
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
        if (requestCode == MODIFY_PW_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK)
        }
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}