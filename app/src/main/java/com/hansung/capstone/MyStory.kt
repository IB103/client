package com.hansung.capstone

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.board.BoardAdapter
import com.hansung.capstone.board.BoardAdapterDecoration
import com.hansung.capstone.board.MyStoryAdapter
import com.hansung.capstone.board.ResultGetPosts
import com.hansung.capstone.databinding.ActivityMystoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStory: AppCompatActivity() {
    private var page = 0
    private lateinit var resultAllPost: RecyclerView
    lateinit var binding: ActivityMystoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMystoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMystory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        resultAllPost =binding.resultAllPostMine
        resultAllPost.addItemDecoration(BoardAdapterDecoration())

        val api = CommunityService.create()
        val swipe =binding.MystorySwipe
        val nickname=MyApplication.prefs.getString("nickname","")
        swipe.setOnRefreshListener {
            api.getPostMyStory(nickname,0)
                .enqueue(object : Callback<ResultGetPosts> {
                    override fun onResponse(
                        call: Call<ResultGetPosts>,
                        response: Response<ResultGetPosts>
                    ) {
                        Log.d("getPostMyStory:", "성공 : ${response.body().toString()}")
                        val body = response.body()
                        runOnUiThread {
                            resultAllPost.adapter=
                                body?.let {it->baseContext?.let{it1-> BoardAdapter(it,it1) }  }
                        }
                    }

                    override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                        Log.d("getPostMyStory:", "실패 : $t")
                    }
                })
            swipe.isRefreshing = false

        }
        Log.d("nickname","${MyApplication.prefs.getString("nickname","")}")
        api.getPostMyStory(nickname = nickname,page=page++)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>
                ) {
                    Log.d("getPostMyStory:", "성공 : ${response.body().toString()}")
                    val body = response.body()
                    runOnUiThread {
                        resultAllPost.adapter=
                            body?.let {it->baseContext?.let{it1-> MyStoryAdapter(it,it1) }  }
                    }
                }
                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getPostMyStory:", "실패 : $t")
                }
            })
//        api.getAllPost(page++)
//            .enqueue(object : Callback<ResultGetPosts> {
//                override fun onResponse(
//                    call: Call<ResultGetPosts>,
//                    response: Response<ResultGetPosts>
//                ) {
//                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
//                    val body = response.body()
//                    runOnUiThread {
//                        resultAllPost.adapter=
//                            body?.let {it->baseContext?.let{it1-> BoardAdapter(it,it1) }  }
//                    }
//                }
//
//                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
//                    Log.d("getAllPost:", "실패 : $t")
//                }
//            })


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