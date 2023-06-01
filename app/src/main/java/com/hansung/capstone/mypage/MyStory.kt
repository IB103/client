package com.hansung.capstone.mypage

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.*
import com.hansung.capstone.board.*
import com.hansung.capstone.databinding.ActivityMystoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyStory: AppCompatActivity() {
    private var page = 0
    private lateinit var resultAllPost: RecyclerView
    lateinit var binding: ActivityMystoryBinding
    var body:ResultGetPosts?=null
    var totalPage=1
    val api = CommunityService.create()
    var adapter= BoardAdapter()
    val nickname= MyApplication.prefs.getString("nickname","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMystoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMystory)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        resultAllPost =binding.resultAllPostMine
        resultAllPost.addItemDecoration(BoardAdapterDecoration())
        resultAllPost.adapter=adapter

        val swipe =binding.MystorySwipe

        swipe.setOnRefreshListener {
            page=0
            checkToken()
            swipe.isRefreshing = false

        }
        resultAllPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤 끝까지 도달 새로운 데이터 로드
                if (!recyclerView.canScrollVertically(1)&&page<totalPage-1){//) {
                   page++
                    checkToken()
                }
            }
        })
        Log.d("nickname", MyApplication.prefs.getString("nickname",""))
        checkToken()

    }
    private fun checkToken(){
        if(Token().checkToken())
        Token().issueNewToken {
            getAllPost(page)
        }else getAllPost(page)
    }
    private fun getAllPost(page:Int){
        val accessToken = MyApplication.prefs.getString("accessToken", "")
        api.getPostMyStory(accessToken = "Bearer $accessToken",nickname,page)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>
                ) {
                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                    body = response.body()
                    totalPage=body!!.totalPage
                    if(page>0)
                   adapter.moreItems(body!!.data as ArrayList<Posts>)
                    else adapter.setInitItems(body!!.data as ArrayList<Posts>)
                }

                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getAllPost:", "실패 : $t")
                }
            })


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
    override fun onResume() {
        super.onResume()
        if(MainActivity.getInstance()?.getCommentCount()!=0|| MainActivity.getInstance()?.getDeletedCommentCount()!=0
        ){

            adapter.commentChanged(MainActivity.getInstance()!!.getChangedPost())
        }else if(  MainActivity.getInstance()?.getHeartCheck()!=-1){
            Log.d("check#1","1")
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