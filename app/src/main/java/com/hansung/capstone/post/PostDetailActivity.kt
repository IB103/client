package com.hansung.capstone.post

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hansung.capstone.R
import com.hansung.capstone.board.BoardAdapter
import com.hansung.capstone.board.GetAllPostInterface
import com.hansung.capstone.board.ResultGetAllPost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val postId = intent.getIntExtra("id",0)

        val title = findViewById<TextView>(R.id.PostTitle)
        val content = findViewById<TextView>(R.id.PostContent)

        val postDetailInterface = GetPostDetailInterface.create()

        postDetailInterface.getPostDetail(postId.toLong())
            .enqueue(object : Callback<ResultGetPostDetail> {
                override fun onResponse(
                    call: Call<ResultGetPostDetail>,
                    response: Response<ResultGetPostDetail>
                ) {
                    Log.d("결과", "성공 : ${response.body().toString()}")
//                    // 값을 넣어야한다~
                    val body = response.body()
                    title.text = body!!.data.title
                    content.text = body.data.content
//                    activity?.runOnUiThread {

                    // 이미지 등록
                    runOnUiThread {
                        val postImageViewpager = findViewById<ViewPager2>(R.id.postImageViewpager)
                        val postImageAdapter = PostImageAdapter()
                        postImageAdapter.imageList = body.data.imageId
                        postImageViewpager.adapter = postImageAdapter
//                            body.data.imageId.let { it -> PostImageAdapter(it) }
                    }
                }

                override fun onFailure(call: Call<ResultGetPostDetail>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })
    }
}