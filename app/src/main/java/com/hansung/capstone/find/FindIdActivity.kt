package com.hansung.capstone.find

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityFindidBinding
import com.hansung.capstone.retrofit.RepFindId
import com.hansung.capstone.retrofit.RetrofitService
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.view_findid.view.*
import kotlinx.android.synthetic.main.view_result_findid.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class FindIdActivity:AppCompatActivity() {
    val service=RetrofitService.create()
    private val binding by lazy { ActivityFindidBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

       // binding.findIdLayout.view_FindId.getBirthday.paintFlags= Paint.UNDERLINE_TEXT_FLAG
        binding.closeView.setOnClickListener { finish() }
        val editText1 = binding.findIdLayout.view_FindId.getName
        val editText2 = binding.findIdLayout.view_FindId.getBirthday
        val myButton = binding.findIdLayout.view_FindId.reqFindId

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                myButton.isEnabled = editText1.text!!.isNotEmpty() && editText2.text!!.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }
        }

        editText1.addTextChangedListener(textWatcher)
        editText2.addTextChangedListener(textWatcher)

        binding.findIdLayout.view_FindId.getBirthday.setOnClickListener {
            val currentDate = Calendar.getInstance()
            val year = currentDate.get(Calendar.YEAR)
            val month = currentDate.get(Calendar.MONTH)
            val day = currentDate.get(Calendar.DAY_OF_MONTH)
            val dialog = SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback { _, year1, monthOfYear, dayOfMonth ->
                    val calendar = Calendar.getInstance()
                    calendar.set(year1, monthOfYear, dayOfMonth)
                    val date =
                        SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(calendar.time)
                    binding.findIdLayout.view_FindId.getBirthday.text = date
                }

                .spinnerTheme(R.drawable.numberpickerstyle)
                .defaultDate(year, month, day)
                .build()

            dialog.show()
        }


    }
    fun reqFindId(view:View){
            val name=binding.findIdLayout.view_FindId.getName.text.toString()
            val birthday=binding.findIdLayout.view_FindId.getBirthday.text.toString()
            service.findID(name,birthday)
                .enqueue(object : Callback<RepFindId> {
                    @SuppressLint("Range", "ResourceAsColor")
                    override fun onResponse(
                        call: Call<RepFindId>,
                        response: Response<RepFindId>,
                    ) {
                        if (response.isSuccessful) {
                            val result: RepFindId = response.body()!!
                            if (response.code() == 200) {
                                if (result.code == 100) {
                                    Log.d("INFO", "아이디 찾기 성공$result")
                                    success(result.data,name)
                                } else {
                                    Log.d("ERR", "아이디 찾기 실패: $result")
                                    message()
                                }
                            }
                        } else {
                            // 통신이 실패한 경우
                            Log.d("ERR findID", "onResponse 실패")
                        }
                    }
                    override fun onFailure(call: Call<RepFindId>, t: Throwable) {
                        Log.d("ERR findId", "onFailure 에러: " + t.message.toString())

                    }
                })

    }
    private fun message(){
        Toast.makeText(this, "회원 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
}
    @SuppressLint("SetTextI18n")
    private fun success(id: List<String>, name:String){
        binding.findIdLayout.view_resultFindId.visibility= View.VISIBLE
        binding.findIdLayout.view_FindId.visibility= View.GONE

        binding.findIdLayout.view_resultFindId.user_Name.text="${name}님의 아이디는"
        binding.findIdLayout.view_resultFindId.user_Id.text = "$id"
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