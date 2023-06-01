package com.hansung.capstone.mypage

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.Token
import com.hansung.capstone.R
import com.hansung.capstone.board.*
import com.hansung.capstone.databinding.ActivityMyscrapBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyScrap : AppCompatActivity() {
    private var page = 0
    private lateinit var resultAllPost: RecyclerView
    lateinit var binding: ActivityMyscrapBinding
    val api = CommunityService.create()
    var body: ResultGetPosts? = null
    var totalPage = 1
    var adapter = BoardAdapter()
    private val id = MyApplication.prefs.getLong("userId", 0)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyscrapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMyscrap)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        resultAllPost = binding.resultAllPostMyscrap
        resultAllPost.addItemDecoration(BoardAdapterDecoration())
        resultAllPost.adapter = adapter

        val swipe = binding.MyscrapSwipe

        swipe.setOnRefreshListener {
            page = 0

            checkToken()
//            api.getPostMyScrap(accessToken = "Bearer $accessToken",id,0).enqueue(object : Callback<ResultGetPosts> {
//                    override fun onResponse(
//                        call: Call<ResultGetPosts>,
//                        response: Response<ResultGetPosts>
//                    ) {
//                        Log.d("getPostMyScrap:", "성공 : ${response.body().toString()}")
//                        val body = response.body()
//                        val board: ArrayList<Posts> = ArrayList()
//                        board.addAll(body!!.data)
//                        adapter.renewItems((body.data as ArrayList<Posts>))
//
//                    }
//
//                    override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
//                        Log.d("getPostMyScrap:", "실패 : $t")
//                    }
//                })
            swipe.isRefreshing = false
        }
        resultAllPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤 끝까지 도달 새로운 데이터 로드
                if (!recyclerView.canScrollVertically(1) && page < totalPage - 1) {
                    page++
                    checkToken()
                }
            }
        })
        checkToken()
//        api.getPostMyScrap(id,page=page++)
//            .enqueue(object : Callback<ResultGetPosts> {
//                override fun onResponse(
//                    call: Call<ResultGetPosts>,
//                    response: Response<ResultGetPosts>
//                ) {
//                    Log.d("getPostMyScrap:", "성공 : ${response.body().toString()}")
//                    val body = response.body()
//                    totalPage=body!!.totalPage
//                    adapter.setInitItems((body!!.data as ArrayList<Posts>))
//                }
//                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
//                    Log.d("getPostMyScrap:", "실패 : $t")
//                }
//            })

    }

    private fun checkToken() {
        if (Token().checkToken()) {
            Token().issueNewToken {
                getAllPost(page)
            }
        } else getAllPost(page)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                overridePendingTransition(0, R.anim.slide_out_right)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    private fun getAllPost(page: Int) {//다음 페이지 요청
        val accessToken = MyApplication.prefs.getString("accessToken", "")
        api.getPostMyScrap(accessToken = "Bearer $accessToken", id, page)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>,
                ) {
                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                    body = response.body()
                    totalPage = body!!.totalPage
                    if (body?.data!!.isNotEmpty()) {
                        if(page>0){
                        adapter.run {
                            moreItems((body!!.data as ArrayList<Posts>))

                        }}else adapter.setInitItems((body!!.data as ArrayList<Posts>))
                    }
                }

                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getAllPost:", "실패 : $t")
                }
            })
        //  },1000)

    }

    override fun onResume() {
        super.onResume()
        if (MainActivity.getInstance()?.getCommentCount() != 0 || MainActivity.getInstance()
                ?.getDeletedCommentCount() != 0
        ) {

            adapter.commentChanged(MainActivity.getInstance()!!.getChangedPost())
        } else if (MainActivity.getInstance()?.getHeartCheck() != -1) {
            Log.d("check#1", "1")
            adapter.heartChanged(MainActivity.getInstance()!!.getChangedPost())
        }
        MainActivity.getInstance()?.stateCheck(-1)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_out_right)
    }
}