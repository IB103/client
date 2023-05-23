package com.hansung.capstone

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.board.BoardAdapter
import com.hansung.capstone.board.BoardAdapterDecoration
import com.hansung.capstone.board.Posts
import com.hansung.capstone.board.ResultGetPosts
import com.hansung.capstone.databinding.ActivitySearchboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchBoardActivity:AppCompatActivity() {
    companion object{
        private var adapter= BoardAdapter()
        private var page=0
        private var totalPage=0
    }
    private lateinit var resultAllPost: RecyclerView
    private val service=CommunityService.create()
    private val binding by lazy{ ActivitySearchboardBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //overridePendingTransition(R.anim.slide_in_right, 0)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        resultAllPost =binding.resultAllPost
        resultAllPost.addItemDecoration(BoardAdapterDecoration())
        resultAllPost.adapter=adapter
        var searchContext:String=""
        val swipe =binding.BoardSwipe
        swipe.setOnRefreshListener {
            page=0
            search(searchContext)
            swipe.isRefreshing = false
        }
        binding.boardSearch.setOnEditorActionListener { _, id, _ ->
            page=0
            if ((id == EditorInfo.IME_ACTION_SEARCH) && (binding.boardSearch.text.toString()
                    .isNotBlank())
            ) {
                searchContext=binding.boardSearch.text.toString()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.boardSearch.windowToken, 0)
               search(searchContext)
            } else {
                binding.boardSearch.requestFocus()
                val manager: InputMethodManager =
                    getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(binding.boardSearch, InputMethodManager.SHOW_IMPLICIT)
                Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            true
        }
        resultAllPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤 끝까지 도달 새로운 데이터 로드
                if (!recyclerView.canScrollVertically(1)) {
                    page++
                    search(searchContext)
                }
            }
        })
    }

    private fun search(str:String){
        service.searchBoard(str,page).enqueue(object : Callback<ResultGetPosts> {
            override fun onResponse(
                call: Call<ResultGetPosts>,
                response: Response<ResultGetPosts>,
            ) {if(response.isSuccessful){
                Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                val resultGetPosts:ResultGetPosts= response.body()!!
                if(resultGetPosts.data?.isNotEmpty()!!){
                adapter.setInitItems((resultGetPosts.data as ArrayList<Posts>))
                }
                //                else{
//                    adapter.removeAll()
//                    comment()
//                }

            }
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
    override fun onDestroy() {
        super.onDestroy()
        adapter.removeAll()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.slide_out_right)
    }
}