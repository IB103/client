package com.hansung.capstone.post

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityPostDetailBinding
import kotlinx.android.synthetic.main.activity_post_detail.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter

class PostDetailActivity : AppCompatActivity() {
    init {
        instance = this
    }

    companion object {
        private var instance: PostDetailActivity? = null
        fun getInstance(): PostDetailActivity? {
            return instance
        }
    }

    private val binding by lazy { ActivityPostDetailBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val postId = intent.getIntExtra("id", 0)

        val api = CommunityService.create()

        api.getPostDetail(postId.toLong())
            .enqueue(object : Callback<ResultGetPostDetail> {
                override fun onResponse(
                    call: Call<ResultGetPostDetail>,
                    response: Response<ResultGetPostDetail>
                ) {
                    Log.d("PostDetail", "성공 : ${response.body().toString()}")
                    val body = response.body()
                    Log.d(
                        "값 조회",
                        "${body?.data?.title} ${body?.data?.content} ${body?.data?.nickname} ${body?.data?.heartButtonCheck}"
                    )
                    binding.PostTitle.text = body?.data?.title
                    binding.PostContent.text = body?.data?.content
                    binding.PostDetailUserName.text = body?.data?.nickname
                    val convertedDate =
                        body?.data?.createdDate?.let { MyApplication.convertDate(it) }
                    val createdDate =
                        convertedDate?.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
                    binding.PostDetailDate.text = createdDate
                    binding.CommentCount.text = body?.data?.commentList?.size.toString()
//                    binding.CommentCount.text = body?.data?.commentList?.size.toString()
//                    binding.CommentCount.text = body?.data?.commentList?.size.toString()
                    binding.BackToList.setOnClickListener {
                        finish()
                    }
                    binding.HeartB.setOnClickListener{
                        if(!body?.data?.heartButtonCheck!!){
                            binding.HeartB.setImageResource(R.drawable.ic_heart_check)
                        }
                    }
                    binding.StarB.setOnClickListener{
                        if(!body?.data?.starButtonCheck!!){
                            binding.StarB.setImageResource(R.drawable.ic_star_check)
                        }
                    }
                    api.getProfileImage(body!!.data.authorProfileImageId)
                        .enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                Log.d("결과", "성공 : ${response.body().toString()}")
                                val imageB = response.body()?.byteStream()
                                val bitmap = BitmapFactory.decodeStream(imageB)
                                binding.PostProfileImage.setImageBitmap(bitmap)
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("결과:", "실패 : $t")
                            }
                        })
                    // 이미지 등록
                    runOnUiThread {
                        when (body.data.imageId.size) {
                            0 -> {
                                binding.ImageLayout.visibility = GONE
                            }
                            else -> {
                                val postImagesAdapter = PostDetailImagesAdapter()
                                postImagesAdapter.imageList = body.data.imageId
                                binding.postImageRecyclerView.adapter = postImagesAdapter
                                postImageRecyclerView.addItemDecoration(
                                    PostImageAdapterDecoration()
                                )
                            }
                        }
                        binding.PostDetailComment.adapter =
                            PostCommentsAdapter(body)
                        binding.PostDetailComment.addItemDecoration(
//                            DividerItemDecoration(
//                                this@PostDetailActivity,
//                                LinearLayoutManager.VERTICAL
//                            )
                        PostCommentsAdapterDecoration()
                        )
                    }
                }

                override fun onFailure(call: Call<ResultGetPostDetail>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })
    }



    fun goImageDetail(imageList: List<Int>, position: Int) {
        val intent = Intent(this, ImageFullScreenActivity::class.java)
        val intArr = imageList.toIntArray()
        intent.putExtra("imageList", intArr)
        intent.putExtra("position", position)
        startActivity(intent)
    }
}