package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.Token
import com.hansung.capstone.databinding.ActivityModifynicknameBinding
import com.hansung.capstone.doublecheck.CheckNick
import com.hansung.capstone.retrofit.RepModifyNick
import com.hansung.capstone.retrofit.ReqModifyNick
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyNickActivity:AppCompatActivity(){
    //private var ModifyActivity: Activity? = null
    val service = RetrofitService.create()
    private val binding by lazy { ActivityModifynicknameBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setContentView(binding.root)
        setSupportActionBar(binding.toobar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.checkNick.setOnClickListener {
            val nickname = binding.modifyNick.text.toString()
            CheckNick().doubleCheckNick(nickname, binding.commentNick, binding.changeBt)
        }
//        binding.checkPW.setOnClickListener {
//            val pw = binding.modifyPW.text.toString()
//            ModifyPW().modifyPW(pw)
//        }
        binding.changeBt.setOnClickListener {
            val nickname = binding.modifyNick.text.toString()
            val modifyNick={modifyNick(nickname)}
            if (Token().checkToken()) {
                Token().issueNewToken(modifyNick)
            }else{
                modifyNick
            }

        }

    }

    private fun modifyNick(nick: String){
        val email= MyApplication.prefs.getString("email","")
        val putReqModifyNick = ReqModifyNick(email, nick)
        service.modifyNick(putReqModifyNick)
            .enqueue(object : Callback<RepModifyNick> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepModifyNick>,
                    response: Response<RepModifyNick>,
                ) {
                    if (response.isSuccessful) {
                        val result: RepModifyNick? = response.body()
                        if (response.code() == 200) {
                            if (result?.code == 100) {
                                Log.d("INFO", "닉네임 변경됨$result")
                                MyApplication.prefs.setString("nickname", nick)
                                val resultIntent = Intent()
                                resultIntent.putExtra("key", true) // 전달할 값 설정
                                setResult(Activity.RESULT_OK, resultIntent)
                                finish()
                            } else {
                                Log.d("ERR", "$result")
                            }
                        }
                    } else {

                        Log.d("ERR modifyNick", "onResponse 실패")
                    }
                }
                override fun onFailure(call: Call<RepModifyNick>, t: Throwable) {

                    Log.d("ERR modifyNick", "onFailure 에러: " + t.message.toString())

                }
            })

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