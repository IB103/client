package com.hansung.capstone

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.board.*
import com.hansung.capstone.databinding.ActivityMyscrapBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScrap: AppCompatActivity() {
    private var page = 0
    private lateinit var resultAllPost: RecyclerView
    lateinit var binding: ActivityMyscrapBinding
    val api = CommunityService.create()
    var body:ResultGetPosts?=null
    var adapter= BoardAdapter()
    private val id=MyApplication.prefs.getLong("userId",0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyscrapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMyscrap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        resultAllPost =binding.resultAllPostMyscrap
        resultAllPost.addItemDecoration(BoardAdapterDecoration())
        resultAllPost.adapter=adapter

        val swipe =binding.MyscrapSwipe

        swipe.setOnRefreshListener {
            page=0
            api.getPostMyScrap(id,0).enqueue(object : Callback<ResultGetPosts> {
                    override fun onResponse(
                        call: Call<ResultGetPosts>,
                        response: Response<ResultGetPosts>
                    ) {
                        Log.d("getPostMyScrap:", "성공 : ${response.body().toString()}")
                        val body = response.body()
                        val board: ArrayList<Posts> = ArrayList()
                        board.addAll(body!!.data)
                        adapter.renewItems((body.data as ArrayList<Posts>))

                    }

                    override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                        Log.d("getPostMyScrap:", "실패 : $t")
                    }
                })
            swipe.isRefreshing = false

        }
        resultAllPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤 끝까지 도달 새로운 데이터 로드
                if (!recyclerView.canScrollVertically(1)) {
                   getAllPost(++page)
                }
            }
        })
        api.getPostMyScrap(id,page=page++)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>
                ) {
                    Log.d("getPostMyScrap:", "성공 : ${response.body().toString()}")
                    val body = response.body()
                    adapter.setInitItems((body!!.data as ArrayList<Posts>))
                }
                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getPostMyScrap:", "실패 : $t")
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
    @Suppress("DEPRECATION")
    private fun getAllPost(page:Int){//다음 페이지 요청
        api.getPostMyScrap(id,page)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>,
                ) {
                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                    body = response.body()
                    if(body?.data!!.isNotEmpty()){
                        adapter.run{
                            moreItems((body!!.data as ArrayList<Posts>))

                        }
                    }
                }
                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getAllPost:", "실패 : $t")
                }
            })
        //  },1000)

    }

}