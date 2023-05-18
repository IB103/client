package com.hansung.capstone.map

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.hansung.capstone.BuildConfig
import com.hansung.capstone.databinding.ActivityWaypointSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WaypointsSearchActivity : AppCompatActivity() {
    val binding by lazy { ActivityWaypointSearchBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.waypointSearchBox.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.waypointSearchBox, InputMethodManager.SHOW_IMPLICIT)

        binding.waypointSearchBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                locationSearch(searchText)
            }
        })

        binding.waypointSearchBox.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                locationSearch(binding.waypointSearchBox.text.toString())
                // 키보드 내리기
                val imm2 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm2.hideSoftInputFromWindow(binding.waypointSearchBox.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.searchBoxClear.setOnClickListener {
            binding.waypointSearchBox.text.clear()
            binding.waypointSearchBox.clearFocus()
            binding.waypointSearchBox.requestFocus()
            val imm3 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm3.showSoftInput(binding.waypointSearchBox, InputMethodManager.SHOW_IMPLICIT)
        }

        binding.goBackButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun locationSearch(text: String) {
        // 카카오 검색 api
        val api = KakaoSearchAPI.create()
        api.getSearchKeyword(
            BuildConfig.KAKAO_REST_API_KEY,
            text
        )
            .enqueue(object : Callback<ResultSearchKeyword> {
                override fun onResponse(
                    call: Call<ResultSearchKeyword>,
                    response: Response<ResultSearchKeyword>
                ) {
                    val body = response.body()
                    if (body != null) {
                        Log.d("getSearchKeyword","body : $body ${body.documents.size}")
                        if (body.documents.isNotEmpty()) {
                            binding.waypointSearchResultRecyclerview.visibility = View.VISIBLE
                            binding.noResult.visibility = View.GONE
                            // 검색 결과 리사이클러뷰에 적용
                            runOnUiThread {
                                binding.waypointSearchResultRecyclerview.adapter =
                                    WaypointsSearchAdapter(this@WaypointsSearchActivity, body)
                            }
                        }
                        else{
                            binding.waypointSearchResultRecyclerview.visibility = View.GONE
                            binding.noResult.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<ResultSearchKeyword>, t: Throwable) {
                    Log.d("getSearchKeyword:", "onFailure : $t")
                }
            })

    }
}