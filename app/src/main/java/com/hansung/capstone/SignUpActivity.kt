package com.hansung.capstone

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hansung.capstone.databinding.ActivitySignupBinding
import com.hansung.capstone.retrofit.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUpActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()
        var server_info = "223.194.133.220:8080" //username password1 password2 email
        var clientBuilder = OkHttpClient.Builder()
        var loggingInterceptor = HttpLoggingInterceptor()
        var retrofit = Retrofit.Builder().baseUrl("http://$server_info/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(clientBuilder.build())
            .build()
        var service = retrofit.create(RetrofitService::class.java)
        //id 중복체크
        binding.DoubleCheck.setOnClickListener() {
            val id = binding.idRegis.text.toString()
            //var getReqDoubleCheckID = ReqDoubleCheckID(id)
            service.doublecheckId(id).enqueue(object : Callback<RepDoubleCheckID> {
                @SuppressLint("Range", "ResourceAsColor")
                override fun onResponse(
                    call: Call<RepDoubleCheckID>,
                    response: Response<RepDoubleCheckID>
                ) {
                    if (response.isSuccessful) {
                        var result: RepDoubleCheckID? = response.body()
                        if (response.code() == 200) {//수정해야함
                            if (result?.msg.equals("GOOD")) {
                                Log.d("INFO", "ID사용가능: " + result?.toString())
                                binding.idConfirm.setText("ID 사용 가능합니다")
                                //binding.idConfirm.setTextColor(R.color.green)
                                binding.submitBt.setEnabled(true)
                            } else {
                                Log.d("ERR", "ID중복: " + result?.toString())
                                binding.idConfirm.setText("ID 사용 불가능합니다")
                                // binding.idConfirm.setTextColor(R.color.red)
                                binding.submitBt.setEnabled(false)
                            }
                        }
                    } else {
                        // 통신이 실패한 경우
                        Log.d("ERR", "onResponse 실패")
                        binding.submitBt.setEnabled(false)
                    }
                }

                override fun onFailure(call: Call<RepDoubleCheckID>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("ERR", "onFailure 에러: " + t.message.toString())
                    binding.submitBt.setEnabled(false)
                }
            })

        }
        //비밀번호 체크
        binding.pwCheck.addTextChangedListener(object : TextWatcher {
            //입력이 끝났을 때
            //4. 비밀번호 일치하는지 확인
            override fun afterTextChanged(p0: Editable?) {
                if (binding.pwRegist.getText().toString()
                        .equals(binding.pwCheck.getText().toString())
                ) {
                    binding.pwConfirm.setText("비밀번호가 일치합니다.")
                    // binding.pwConfirm.setTextColor(R.color.green)
                    // 가입하기 버튼 활성화
                    binding.submitBt.isEnabled = true
                } else {
                    binding.pwConfirm.setText("비밀번호가 일치하지 않습니다.")
                    //  binding.pwConfirm.setTextColor(R.color.red)
                    // 가입하기 버튼 비활성화
                    binding.submitBt.isEnabled = false
                }
            }
            //입력하기 전
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            //텍스트 변화가 있을 시
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.pwRegist.getText().toString()
                        .equals(binding.pwCheck.getText().toString())
                ) {
                    binding.pwConfirm.setText("비밀번호가 일치합니다.")
                    //binding.pwConfirm.setTextColor(R.color.green)
                    // 가입하기 버튼 활성화
                    binding.submitBt.isEnabled = true
                } else {
                    binding.pwConfirm.setText("비밀번호가 일치하지 않습니다.")
                    //binding.pwConfirm.setTextColor(R.color.red)
                    // 가입하기 버튼 비활성화
                    binding.submitBt.isEnabled = false
                }
            }
        })
        //회원가입 버튼
        binding.submitBt.setOnClickListener {
            val email = binding.idRegis.getText().toString()
            val password = binding.pwRegist.getText().toString()
            val nickname = binding.NickName.getText().toString()
            val username = binding.UserName.getText().toString()
            val birthday = binding.Birthday.getText().toString()

            var postReqRegister = ReqRegister(email, password, nickname, username, birthday)
            service.register(postReqRegister).enqueue(object : Callback<RepRegister> {

                @SuppressLint("Range")
                override fun onResponse(call: Call<RepRegister>, response: Response<RepRegister>) {
                    if (response.isSuccessful) {
                        Log.d("req", "OK")
                        var result: RepRegister? = response.body()
                        if (response.code() == 200) {
                            //  if(!result?.nickname.isNullOrEmpty()){
                            Log.d("INFO", "성공: " + result?.toString())
                            Log.d("INFO", "닉네임" + result?.nickname)
                            Log.d("INFO", "ID" + result?.id)
                            //startMainActivity()
                            // binding.loginbt.setEnabled(true)
                            // finish()
                        } else {
                            Log.d("ERR", "실패: " + result?.toString())
                            // binding.loginbt.setEnabled(false)
                        }
                    } else {
                        // 통신이 실패한 경우(응답코드 3xx, 4xx 등)
                        Log.d("ERR", "onResponse 실패")
                        //   binding.loginbt.setEnabled(false)
                    }
                }

                override fun onFailure(call: Call<RepRegister>, t: Throwable) {
                    // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                    Log.d("ERR", "onFailure 에러: " + t.message.toString())
                    // binding.loginbt.setEnabled(false)
                }
            })

        }
        //닉네임 중복 체크
        binding.doublechecknickname.setOnClickListener {
            val nickname = binding.NickName.text.toString()
            service.doublecheckNickName(nickname)
                .enqueue(object : Callback<RepDoubleCheckNickName> {
                    @SuppressLint("Range", "ResourceAsColor")
                    override fun onResponse(
                        call: Call<RepDoubleCheckNickName>,
                        response: Response<RepDoubleCheckNickName>
                    ) {
                        if (response.isSuccessful) {
                            var result: RepDoubleCheckNickName? = response.body()
                            if (response.code() == 200) {//수정해야함
                                if (result?.msg.equals("GOOD")) {
                                    Log.d("INFO", "닉네임 사용가능: " + result?.toString())
                                    binding.nicknameConfirm.setText("닉네임 사용 가능합니다")
                                    //binding.nicknameConfirm.setTextColor(R.color.green)
                                    binding.submitBt.setEnabled(true)
                                } else {
                                    Log.d("ERR", "닉네임 중복: " + result?.toString())
                                    binding.nicknameConfirm.setText("ID 사용 불가능합니다")
                                    //binding.nicknameConfirm.setTextColor(R.color.red)
                                    binding.submitBt.setEnabled(false)
                                }
                            }
                        } else {
                            // 통신이 실패한 경우
                            Log.d("ERR", "onResponse 실패")
                            binding.submitBt.setEnabled(false)
                        }
                    }

                    override fun onFailure(call: Call<RepDoubleCheckNickName>, t: Throwable) {
                        // 통신 실패 (인터넷 끊킴, 예외 발생 등 시스템적인 이유)
                        Log.d("ERR", "onFailure 에러: " + t.message.toString())
                        binding.submitBt.setEnabled(false)
                    }
                })
        }
    }
    public fun register(){

    }
}