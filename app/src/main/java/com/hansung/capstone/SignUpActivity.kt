package com.hansung.capstone

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.databinding.ActivitySignupBinding
import com.hansung.capstone.retrofit.*
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SignUpActivity:AppCompatActivity() {
    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //var binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val service = RetrofitService.create()

        binding.DoubleCheck.setOnClickListener {
            val id = binding.idRegis.text.toString()
            Log.d("id", id)
            //var getReqDoubleCheckID = ReqDoubleCheckID(id)
            service.doublecheckId(id).enqueue(object : Callback<RepDoubleCheckID> {
                @SuppressLint("Range", "ResourceAsColor", "SetTextI18n")
                override fun onResponse(
                    call: Call<RepDoubleCheckID>,
                    response: Response<RepDoubleCheckID>
                ) {
                    if (response.isSuccessful) {
                        val result: RepDoubleCheckID = response.body()!!
                        if (result.code == 100) {
                                Log.d("INFO", "OK: $result")
                            binding.idConfirm.text = "ID 사용 가능합니다"
                                //binding.idConfirm.setTextColor(R.color.green)
                            binding.submitBt.isEnabled = true
                            } else {
                                Log.d("ERR", "FAIL: $result")
                            binding.idConfirm.text = "ID 사용 불가능합니다"
                                // binding.idConfirm.setTextColor(R.color.red)
                            binding.submitBt.isEnabled = false
                            }
                        }
                    else {
                        // 통신이 실패한 경우
                        Log.d("ERR", "onResponse 실패")
                        binding.submitBt.isEnabled = false
                    }
                }

                override fun onFailure(call: Call<RepDoubleCheckID>, t: Throwable) {
                    Log.d("ERR", "onFailure 에러: " + t.message.toString())
                    binding.submitBt.isEnabled = false
                }
            })

        }
        binding.Birthday.setOnClickListener {

            val currentDate = Calendar.getInstance()
            val setYear = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val day = currentDate.get(Calendar.DAY_OF_MONTH)

            val dialog = SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback { _, year, monthOfYear, dayOfMonth ->
                    title = "생년월일"
                    val calendar = Calendar.getInstance()
                    calendar.set(year, monthOfYear, dayOfMonth)
                    val date =
                        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.time)

                    binding.Birthday.text = date
                }

                .spinnerTheme(R.drawable.numberpickerstyle)
                .defaultDate(setYear, month, day)
                .build().apply {
                    //.show()
                    setTitle("생년월일")
                    //show()
                }
            dialog.show()
            // showDatePicker()
        }

        //pw check
        binding.pwCheck.addTextChangedListener(object : TextWatcher {
            //입력이 끝났을 때
            //4.pw check
            override fun afterTextChanged(p0: Editable?) {
                if (binding.pwRegist.text.toString() == binding.pwCheck.text.toString()
                ) {
                    binding.pwConfirm.text = "비밀번호가 일치합니다."
                    // binding.pwConfirm.setTextColor(R.color.green)
                    // 가입 버튼 활성화
                    binding.submitBt.isEnabled = true
                } else {
                    binding.pwConfirm.text = "비밀번호가 일치하지 않습니다."
                    //  binding.pwConfirm.setTextColor(R.color.red)
                    // 가입 버튼 활성화 안한다
                    binding.submitBt.isEnabled = false
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            //텍스트 변화가 있을 시
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.pwRegist.text.toString() == binding.pwCheck.text.toString()
                ) {
                    binding.pwConfirm.text = "비밀번호가 일치합니다."
                    binding.pwConfirm.setTextColor(Color.parseColor("#04B431"))
                    //binding.pwConfirm.setTextColor(R.color.green)
                    // 가입 버튼 활성화
                    binding.submitBt.isEnabled = true
                } else {
                    binding.pwConfirm.text = "비밀번호가 일치하지 않습니다."
                    binding.pwConfirm.setTextColor(Color.parseColor("#FF0000"))
                    //binding.pwConfirm.setTextColor(R.color.red)
                    // 가입 버튼 활성화 안한다
                    binding.submitBt.isEnabled = false
                }
            }
        })

        binding.submitBt.setOnClickListener {
            val email = binding.idRegis.text.toString()
            val password = binding.pwRegist.text.toString()
            val nickname = binding.NickName.text.toString()
            val username = binding.UserName.text.toString()
            val birthday = binding.Birthday.text.toString()

            val postReqRegister = ReqRegister(email, password, nickname, username, birthday)
            service.register(postReqRegister).enqueue(object : Callback<RepRegister> {
                @SuppressLint("Range")
                override fun onResponse(call: Call<RepRegister>, response: Response<RepRegister>) {
                    if (response.isSuccessful) {
                        Log.d("req", "OK")
                        val result: RepRegister? = response.body()
                        if (response.code() == 200) {
                            //  if(!result?.nickname.isNullOrEmpty()){
                            Log.d("INFO", "성공: " + result?.toString())
                            Log.d("INFO", "닉네임" + result?.nickname)
                            Log.d("INFO", "ID" + result?.id)
                            //startMainActivity()
                            // finish()
                        } else {
                            Log.d("ERR", "실패: " + result?.toString())
                        }
                    } else {

                        Log.d("ERR", "onResponse 실패")
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
                            val result: RepDoubleCheckNickName = response.body()!!
                            if (result.code == 100) {
                                    Log.d("INFO", "닉네임 OK: $result")
                                binding.nicknameConfirm.text = "닉네임 사용 가능합니다"
                                binding.nicknameConfirm.setTextColor(Color.parseColor("#04B431"))
                                binding.submitBt.isEnabled = true
                                } else {
                                    Log.d("ERR", "닉네임 중복: $result")
                                binding.nicknameConfirm.text = "닉네임 사용 불가능합니다"
                                  binding.nicknameConfirm.setTextColor(Color.parseColor("#FF0000"))
                                    //binding.nicknameConfirm.setTextColor(R.color.red)
                                binding.submitBt.isEnabled = false
                                }
                        } else {
                            // 통신이 실패한 경우
                            Log.d("ERR", "onResponse 실패")
                            binding.submitBt.isEnabled = false
                        }
                    }
                    override fun onFailure(call: Call<RepDoubleCheckNickName>, t: Throwable) {
                        Log.d("ERR", "onFailure 에러: " + t.message.toString())
                        binding.submitBt.isEnabled = false
                    }
                })
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