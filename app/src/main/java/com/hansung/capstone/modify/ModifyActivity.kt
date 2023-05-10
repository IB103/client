package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityModifypwBinding
import com.hansung.capstone.retrofit.RepModifyPW
import com.hansung.capstone.retrofit.ReqModifyPW
import com.hansung.capstone.retrofit.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyActivity:AppCompatActivity() {
    val service=RetrofitService.create()
    private val binding by lazy { ActivityModifypwBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.closeView.setOnClickListener { finish() }
        binding.getLastPw.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (binding.getFirstPw.text.toString() == binding.getLastPw.text.toString()
                ) {
                    binding.commentPw.text = "비밀번호가 일치합니다."
                     binding.commentPw.setTextColor(Color.parseColor("#04B431"))
                    binding.modifyPw.isEnabled = true
                } else {
                    binding.commentPw.text = "비밀번호가 일치하지 않습니다."
                    binding.commentPw.setTextColor(Color.parseColor("#FF0000"))
                    binding.modifyPw.isEnabled = false
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.getFirstPw.text.toString() == binding.getLastPw.text.toString()
                ) {
                    binding.commentPw.text = "비밀번호가 일치합니다."
                    binding.commentPw.setTextColor(Color.parseColor("#04B431"))
                    binding.modifyPw.isEnabled = true
                } else {
                    binding.commentPw.text = "비밀번호가 일치하지 않습니다."
                    binding.commentPw.setTextColor(Color.parseColor("#FF0000"))
                    binding.modifyPw.isEnabled = false
                }
            }
        })
    }
    fun modifyPw(view: View){
        val pw=binding.getLastPw.text.toString()
        val email= MyApplication.prefs.getString("id","")
        val postReqModifyPW = ReqModifyPW(email, pw)
        service.modifyPW(postReqModifyPW).enqueue(object : Callback<RepModifyPW> {
            @SuppressLint("Range", "ResourceAsColor")
            override fun onResponse(
                call: Call<RepModifyPW>,
                response: Response<RepModifyPW>,
            ) {
                if (response.isSuccessful) {
                    val result: RepModifyPW = response.body()!!
                    if (response.code() == 200) {//수정해야함
                        if (result.code == 100) {

                        } else {
                            Log.d("ERR", "$result")
                        }
                    }
                } else {
                    // 통신이 실패한 경우
                    Log.d("ERR ModifyPW", "onResponse 실패")
                }
            }
            override fun onFailure(call: Call<RepModifyPW>, t: Throwable) {
                Log.d("ERR ModifyPw", "onFailure 에러: " + t.message.toString())
            }
        })

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