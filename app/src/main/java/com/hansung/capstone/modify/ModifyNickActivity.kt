package com.hansung.capstone.modify

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityModifynickBinding
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
    lateinit var binding: ActivityModifynickBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        //ModifyActivity = this
        binding = ActivityModifynickBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.changeBt.isEnabled = false
        binding.checkNick.setOnClickListener {
            val nickname = binding.modifyNick.text.toString()
            CheckNick().doubleCheckNick(nickname, binding.commentNick, binding.changeBt)
        }
        binding.checkPW.setOnClickListener {
            val pw = binding.modifyPW.text.toString()
            ModifyPW().modifyPW(pw)
        }
        binding.changeBt.setOnClickListener {
            val nickname = binding.modifyNick.text.toString()
            modifyNick(nickname)
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
                                Log.d("changeNick", MyApplication.prefs.getString("nickname",""))
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                Log.d("ERR", "$result")

                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("ERR modifyNick", "onResponse 실패")

                    }
                }
                override fun onFailure(call: Call<RepModifyNick>, t: Throwable) {

                    Log.d("ERR modifyNick", "onFailure 에러: " + t.message.toString())

                }
            })

    }

}