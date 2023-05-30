package com.hansung.capstone

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
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
    }
    private lateinit var resultAllPost: RecyclerView
    private val service=CommunityService.create()
    private var searchMode=0
    var totalPage=0

    private val binding by lazy{ ActivitySearchboardBinding.inflate(layoutInflater) }
    @SuppressLint("ResourceAsColor")
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
            if (searchMode == 0) search(searchContext) else searchNickname(searchContext)
            swipe.isRefreshing = false
        }
        binding.searchBoxClear2.setOnClickListener {
            binding.boardSearch.text.clear()
            binding.boardSearch.clearFocus()
            binding.boardSearch.requestFocus()
            val imm3 = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm3.showSoftInput(binding.boardSearch, InputMethodManager.SHOW_IMPLICIT)
        }
        binding.boardSearch.setOnEditorActionListener { _, id, _ ->
            page=0
            if ((id == EditorInfo.IME_ACTION_SEARCH) && (binding.boardSearch.text.toString()
                    .isNotBlank())
            ) {
                searchContext=binding.boardSearch.text.toString()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.boardSearch.windowToken, 0)
                if (searchMode == 0) search(searchContext) else searchNickname(searchContext)

            } else {
                binding.boardSearch.requestFocus()
                val manager: InputMethodManager =
                    getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(binding.boardSearch, InputMethodManager.SHOW_IMPLICIT)
                Toast.makeText(this, "검색어를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            true
        }
     binding.searchAuthor.setOnClickListener {
         page=0
         searchMode=1
         searchNickname(searchContext)
         binding.searchAuthor.setTextColor(Color.parseColor("#87D5AA"))
         binding.searchAuthorView.setBackgroundColor(Color.parseColor("#87D5AA"))
         binding.searchTitleContent.setTextColor(Color.parseColor("#A4A4A4"))
         binding.searchTitleContentView.setBackgroundColor(Color.parseColor("#A4A4A4"))
     }
        binding.searchTitleContent.setOnClickListener {
            page=0
            searchMode=0
            search(searchContext)
            binding.searchAuthor.setTextColor(Color.parseColor("#A4A4A4"))
            binding.searchAuthorView.setBackgroundColor(Color.parseColor("#A4A4A4"))
            binding.searchTitleContent.setTextColor(Color.parseColor("#87D5AA"))
            binding.searchTitleContentView.setBackgroundColor(Color.parseColor("#87D5AA"))
        }



        resultAllPost.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)&&page<totalPage-1) {
                    page++
                    if (searchMode == 0) search(searchContext) else searchNickname(searchContext)
                }
            }


        })
    }

    private fun search(str:String){
        Log.d("checking3!","${page}")
        service.searchBoard(str,page).enqueue(object : Callback<ResultGetPosts> {
            override fun onResponse(
                call: Call<ResultGetPosts>,
                response: Response<ResultGetPosts>
            ) {if(response.isSuccessful){
                Log.d("getAllPost:", "성공 : ${response.body().toString()}")
                val resultGetPosts:ResultGetPosts= response.body()!!
                totalPage=resultGetPosts.totalPage
                if(resultGetPosts.data?.isNotEmpty()!!){
                    binding.noResultComment.visibility= View.GONE
                    binding.noResultImage.visibility= View.GONE
                    if(page==0)
                        adapter.setInitItems((resultGetPosts.data as ArrayList<Posts>))
                    else adapter.moreItems((resultGetPosts.data as ArrayList<Posts>))
                }else{
                    adapter.removeAll()
                    binding.noResultComment.visibility= View.VISIBLE
                    binding.noResultImage.visibility= View.VISIBLE
                }
            }
            }
            override fun onFailure(call: Call<ResultGetPosts>, t: Throwable) {
                Log.d("getAllPost:", "실패 : $t")
            }
        })
    }
    private fun searchNickname(str:String){
        Log.d("checking4!","${page}")
        service.searchNickname(str,page).enqueue(object : Callback<ResultGetPosts> {
            override fun onResponse(
                call: Call<ResultGetPosts>,
                response: Response<ResultGetPosts>
            ) {if(response.isSuccessful){
                Log.d("searchNickname:", "성공 : ${response.body().toString()}")
                val resultGetPosts:ResultGetPosts= response.body()!!
                totalPage=resultGetPosts.totalPage
                if(resultGetPosts.data?.isNotEmpty()!!){
                    binding.noResultComment.visibility= View.GONE
                    binding.noResultImage.visibility= View.GONE
                    if(page==0)
                        adapter.setInitItems((resultGetPosts.data as ArrayList<Posts>))
                    else adapter.moreItems((resultGetPosts.data as ArrayList<Posts>))
                }
            }else {

                adapter.removeAll()
                binding.noResultComment.visibility= View.VISIBLE
                binding.noResultImage.visibility= View.VISIBLE}
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

    override fun onResume() {
        super.onResume()
        if(MainActivity.getInstance()?.getCommentCount()!=0||MainActivity.getInstance()?.getDeletedCommentCount()!=0
        ){
            adapter.commentChanged(MainActivity.getInstance()!!.getChangedPost())
        }else if(  MainActivity.getInstance()?.getHeartCheck()!=-1){
            Log.d("check#1","1")
            adapter.heartChanged(MainActivity.getInstance()!!.getChangedPost())
        }
        MainActivity.getInstance()?.stateCheck(-1)
    }
}