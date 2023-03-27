package com.hansung.capstone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hansung.capstone.board.BoardAdapter
import com.hansung.capstone.board.BoardAdapterDecoration
import com.hansung.capstone.board.Posts
import com.hansung.capstone.board.ResultGetPosts
import com.hansung.capstone.databinding.FragmentBoardBinding
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BoardFragment : Fragment() {
    lateinit var  binding: FragmentBoardBinding
    private lateinit var resultAllPost: RecyclerView
    private var page = 0
    var body:ResultGetPosts? = null
    val api = CommunityService.create()
    private val linearLayoutManager= LinearLayoutManager(activity)
    private lateinit var adapter: BoardAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentBoardBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= BoardAdapter()
        resultAllPost = view.findViewById(R.id.resultAllPost)
        resultAllPost.addItemDecoration(BoardAdapterDecoration())
        val swipe = view.findViewById<SwipeRefreshLayout>(R.id.BoardSwipe)
        swipe.setOnRefreshListener {
            renewPage()

            swipe.isRefreshing = false
        }


        resultAllPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // 스크롤이 끝까지 도달하면 새로운 데이터 로드
                if (!recyclerView.canScrollVertically(1)&&page<body!!.totalPage) {
                    getallPost(++page)
                }
            }
        })
        initData()//첫 페이지 목록
        resultAllPost.adapter=adapter
        resultAllPost.layoutManager=linearLayoutManager
        binding.postB.setOnClickListener{
            Log.d("postbt","clicked")
            val intent = Intent(activity, WriteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun renewPage() {
        page=0
        api.getAllPost(0)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>,
                ) {
                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                    body = response.body()
                    //board.addAll(body!!.data)
                    adapter.renewItems((body!!.data as ArrayList<Posts>))
                    // board.removeAll()
//                        activity?.runOnUiThread {
//                            resultAllPost.adapter =
//                                body?.let { it -> BoardAdapter(it)  }
//                        }
                }
                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getAllPost:", "실패 : $t")
                }
            })
    }

    fun initData() {
        Log.d("initData","#################")
        // adapter= BoardAdapter()
        api.getAllPost(0)
            .enqueue(object : Callback<ResultGetPosts> {
                override fun onResponse(
                    call: Call<ResultGetPosts>,
                    response: Response<ResultGetPosts>,
                ) {
                    Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                    body = response.body()
                    if(body?.data?.isNotEmpty()!!){
                        adapter.setInitItems((body!!.data as ArrayList<Posts>))
                    }
                }
                override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                    Log.d("getAllPost:", "실패 : $t")
                }
            })
    }
    @Suppress("DEPRECATION")
    private fun getallPost(pagenum:Int){//다음 페이지 요청
        //   adapter.setLoadingView(true)
        // val handler = Handler()
        // handler.postDelayed({
        api.getAllPost(pagenum)
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
    override fun onResume() {
        super.onResume()
        if(MainActivity.getInstance()?.getdeleteCheck()==true){
            Toast.makeText(activity, "게시글이 삭제됐습니다", Toast.LENGTH_SHORT).show()
            renewPage()
            MainActivity.getInstance()?.deleteCheck(false)
        }
        if(MainActivity.getInstance()?.getwriteCheck()==true){
            Toast.makeText(activity, "게시글이 등록됐습니다", Toast.LENGTH_SHORT).show()
            renewPage()
            MainActivity.getInstance()?.writeCheck(false)
        }
        //수정해야함
//        if(MainActivity.getInstance()?.getChangedPostCheck() == true){
//            Log.d("commentChekc","OK")
//            adapter.reLoad()
//            MainActivity.getInstance()?.setChangedPostCheck(false)
//        }
    }

}


