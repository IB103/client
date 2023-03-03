package com.hansung.capstone.post

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.bumptech.glide.Glide
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
    val api = CommunityService.create()
    lateinit var body: ResultGetPostDetail
    var noImage=-1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val postId = intent.getIntExtra("id", 0)
        binding.imageButton.setOnClickListener {
            val comment = binding.InsertComment.text.toString()
            binding.InsertComment.text = null
            softkeyboardHide()
            PostComment(this@PostDetailActivity).postComment(comment, postId, binding)
            getPostDetails(postId.toLong())
//            api.getPostDetail(postId.toLong())
//                .enqueue(object : Callback<ResultGetPostDetail> {
//                    @SuppressLint("SetTextI18n")
//                    override fun onResponse(
//                        call: Call<ResultGetPostDetail>,
//                        response: Response<ResultGetPostDetail>,
//                    ) {
//                        val body = response.body()
//                        var count = 0
//                        for (i in body?.data?.commentList!!) {
//                            count += i.reCommentList.size
//                        }
//                        count += body.data.commentList.size
//                        binding.CommentCount.text = count.toString()
//                        Log.d("check", "############################")
//                        // 이미지 등록
//                        runOnUiThread {
//                            binding.PostDetailComment.adapter =
//                                PostCommentsAdapter(body, this@PostDetailActivity)
//                        }
//                    }
//
//                    override fun onFailure(call: Call<ResultGetPostDetail>, t: Throwable) {
//                        Log.d("getPostDetail:", "실패 : $t")
//                    }
//                })
//                binding.PostDetailComment.adapter =PostCommentsAdapter(body,this@PostDetailActivity )

        }
        getPostDetails(postId.toLong())
    }

    private fun getPostDetails(postId:Long) {
        api.getPostDetail(postId)
            .enqueue(object : Callback<ResultGetPostDetail> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResultGetPostDetail>,
                    response: Response<ResultGetPostDetail>,
                ) {


                    body = response.body()!!
                    Log.d("getPostDetails","##############${body}")
                    Log.d("type", "${body?.javaClass}")
                    binding.PostTitle.text = body?.data?.title
                    binding.PostContent.text = body?.data?.content
                    binding.PostDetailUserName.text = body?.data?.nickname

                    val convertedDate =
                        body?.data?.createdDate?.let { MyApplication.convertDate(it) }
                    val createdDate =
                        convertedDate?.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
                    binding.PostDetailDate.text = createdDate
                    var count = 0
                    for (i in body?.data?.commentList!!) {
                        count += i.reCommentList.size
                    }
                    count += body.data.commentList.size
//                    Log.d("카운트", "$count")
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
                        api.checkFavorite(12, 94)
                            .enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(
                                    call: Call<ResponseBody>,
                                    response: Response<ResponseBody>,
                                ) {
                                    Log.d("checkFavorite", "성공 : ${response.body().toString()}")
//                                    body?.data?.postVoterId?.let { it1 -> heartChange(it1) }
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    Log.d("checkFavorite:", "실패 : $t")
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
                    Log.d("authorProfileImageId","${body.data.authorProfileImageId}")
                    if(body.data.authorProfileImageId!=noImage.toLong()) {
                        Glide.with(this@PostDetailActivity)
                            .load("${MyApplication.getUrl()}profile-image/${body.data.authorProfileImageId}") // 불러올 이미지 url
                            .override(100, 100)
                            .circleCrop() // 동그랗게 자르기
                            .into(binding.PostProfileImage) // 이미지를 넣을 뷰
                    }else binding.PostProfileImage.setImageResource(R.drawable.user)

                    // 이미지 등록
                    runOnUiThread {
                        if (body.data.imageId.isNotEmpty()) {
                            binding.ImageLayout.visibility = VISIBLE
                            val postImagesAdapter = PostDetailImagesAdapter(this@PostDetailActivity)
                            postImagesAdapter.imageList = body.data.imageId
                            binding.postImageRecyclerView.adapter = postImagesAdapter
                            postImageRecyclerView.addItemDecoration(
                                PostImageAdapterDecoration()
                            )
                        }
                        binding.PostDetailComment.adapter =
                            PostCommentsAdapter(body, this@PostDetailActivity)
                        (binding.PostDetailComment.adapter as PostCommentsAdapter).notifyDataSetChanged()
                        binding.PostDetailComment.addItemDecoration(
                            PostCommentsAdapterDecoration()
                        )
                    }
                }

                override fun onFailure(call: Call<ResultGetPostDetail>, t: Throwable) {
                    Log.d("getPostDetail:", "실패 : $t")
                }
            })
//        api.getPostDetail(postId)
//            .enqueue(object : Callback<ResultGetPostDetail> {
//                @SuppressLint("SetTextI18n")
//                override fun onResponse(
//                    call: Call<ResultGetPostDetail>,
//                    response: Response<ResultGetPostDetail>,
//                ) {
//                    val body = response.body()
//                    var count = 0
//                    for (i in body?.data?.commentList!!) {
//                        count += i.reCommentList.size
//                    }
//                    count += body.data.commentList.size
//                    binding.CommentCount.text = count.toString()
//                    Log.d("check", "############################")
//                    // 이미지 등록
//                    runOnUiThread {
//                        binding.PostDetailComment.adapter =
//                            PostCommentsAdapter(body, this@PostDetailActivity)
//                    }
//                }
//
//                override fun onFailure(call: Call<ResultGetPostDetail>, t: Throwable) {
//                    Log.d("getPostDetail:", "실패 : $t")
//                }
//            })
    }

    fun softkeyboardHide() {
        //   val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    }

    fun goImageDetail(imageList: List<Int>, position: Int) {
        val intent = Intent(this, ImageFullScreenActivity::class.java)
        val intArr = imageList.toIntArray()
        intent.putExtra("imageList", intArr)
        intent.putExtra("position", position)
        startActivity(intent)
    }


}



