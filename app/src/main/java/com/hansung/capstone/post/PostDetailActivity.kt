package com.hansung.capstone.post

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import androidx.appcompat.app.AppCompatActivity
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
        var buttonCheck: Int = 0
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
                @SuppressLint("SetTextI18n")
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
                    var count = 0
                    for (i in body?.data?.commentList!!){
                        count+=i.reCommentList.size
                    }
                    count+= body.data.commentList.size
                    Log.d("카운트","$count")
//                    binding.CommentCount.text = body.data.commentList.size.toString()
                    binding.CommentCount.text = count.toString()
                    var heartCount = body.data.postVoterId.size
                    binding.HeartCount.text = heartCount.toString()
                    binding.BackToList.setOnClickListener {
                        finish()
                    }

                    // 좋아요 버튼
                    buttonCheck = if (body.data.postVoterId.contains(12)) {
                        binding.HeartB.setImageResource(R.drawable.ic_heart_check)
                        1
                    } else {
                        binding.HeartB.setImageResource(R.drawable.ic_heart_no_check)
                        0
                    }
                    binding.HeartB.setOnClickListener {
                        api.checkFavorite(12,94)
                            .enqueue(object : Callback<ResponseBody> {
                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                Log.d("좋아요 겟", "성공 : ${response.body().toString()}")
//                                    body?.data?.postVoterId?.let { it1 -> heartChange(it1) }
                            }

                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                Log.d("결과:", "실패 : $t")
                            }
                        })
                        runOnUiThread {
                            when (buttonCheck) {
                                0 -> {
                                    binding.HeartB.setImageResource(R.drawable.ic_heart_check)
                                    binding.HeartCount.text = "${++heartCount}"
                                    buttonCheck = 1
                                }
                                else -> {
                                    binding.HeartB.setImageResource(R.drawable.ic_heart_no_check)
                                    binding.HeartCount.text = "${--heartCount}"
                                    buttonCheck = 0
                                }
                            }
                        }
                    }
                    api.getProfileImage(body.data.authorProfileImageId)
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