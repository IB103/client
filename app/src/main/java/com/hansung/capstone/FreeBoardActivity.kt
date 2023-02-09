package com.hansung.capstone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ActivityFreeboardBinding
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FreeBoardActivity : AppCompatActivity() {
    init{
        instance = this
    }
    companion object{
        private var instance: FreeBoardActivity? = null
        fun getInstance(): FreeBoardActivity? {
            return instance
        }
    }
    fun setFragment(post: Posts) {
        val transaction = supportFragmentManager.beginTransaction()
            .add(R.id.postDetailFragment, PostDetailFragment(post))
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFreeboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.resultAllPost.layoutManager = LinearLayoutManager(this)

        val allPostInterface = GetAllPostInterface.create()

        allPostInterface.getAllPost(0)
            .enqueue(object : Callback<ResultGetAllPost> {
                override fun onResponse(
                    call: Call<ResultGetAllPost>,
                    response: Response<ResultGetAllPost>
                ) {
                    Log.d("결과", "성공 : ${response.body().toString()}")
//                    // 값을 넣어야한다~
                    val body = response.body()
                    runOnUiThread {
                        binding.resultAllPost.adapter =
                            body?.let { it -> FreeBoardAdapter(it) }
                    }
                }

                override fun onFailure(call: Call<ResultGetAllPost>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })
    }
}