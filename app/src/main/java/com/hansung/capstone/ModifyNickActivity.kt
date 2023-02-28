package com.hansung.capstone

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.databinding.ActivityModifynickBinding
import com.hansung.capstone.doublecheck.checkNick
import com.hansung.capstone.modify.modifyNick
import com.hansung.capstone.modify.modifyPW
import com.hansung.capstone.retrofit.RepModifyNick
import com.hansung.capstone.retrofit.ReqModifyNick
import com.hansung.capstone.retrofit.RetrofitService
import kotlinx.android.synthetic.main.activity_modifynick.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.delay as delay1

class ModifyNickActivity:AppCompatActivity(){
    var ModifyActivity: Activity? = null
    val fragmentOne:MyPageFragment=MyPageFragment()
    lateinit var binding: ActivityModifynickBinding
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    override fun onCreate(savedInstanceState: Bundle?) {
        ModifyActivity = this
        binding = ActivityModifynickBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.changeBt.isEnabled = false
        binding.checkNick.setOnClickListener {
            val nickname = binding.modifyNick.text.toString()
            checkNick().doublecheckNick(nickname, binding.commentNick, binding.changeBt)
        }
        binding.checkPW.setOnClickListener {
            val pw = binding.modifyPW.text.toString()
            modifyPW().modifyPW(pw)
        }
        binding.changeBt.setOnClickListener {
            val nickname = binding.modifyNick.text.toString()
            modifyNick(nickname)
        }
    }

    var server_info = MyApplication.getUrl() //username password1 password2 email
    var clientBuilder = OkHttpClient.Builder()
    var retrofit = Retrofit.Builder().baseUrl("$server_info")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(clientBuilder.build())
        .build()
    var service = retrofit.create(RetrofitService::class.java)
    fun modifyNick(nick: String){
        val email=MyApplication.prefs.getString("email","")
        var putReqModifyNick = ReqModifyNick(email, nick)
        service.modifyNick(putReqModifyNick)
            .enqueue(object : Callback<RepModifyNick> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepModifyNick>,
                    response: Response<RepModifyNick>,
                ) {
                    if (response.isSuccessful) {
                        var result: RepModifyNick? = response.body()
                        if (response.code() == 200) {//수정해야함
                            if (result?.code == 100) {
                                Log.d("INFO", "닉네임 변경됨" + result?.toString())
                                MyApplication.prefs.setString("nickname","$nick")
                                Log.d("changeNick","${MyApplication.prefs.getString("nickname","")}")
                                setResult(RESULT_OK)
                                finish()
                            } else {
                                Log.d("ERR", "닉네임 변경불가: " + result?.toString())

                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("ERR modiftNick", "onResponse 실패")

                    }
                }
                override fun onFailure(call: Call<RepModifyNick>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("ERR modifyNick", "onFailure 에러: " + t.message.toString())

                }
            })

    }

}