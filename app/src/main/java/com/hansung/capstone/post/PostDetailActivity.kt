package com.hansung.capstone.post

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.hansung.capstone.*
import com.hansung.capstone.board.ResDelete
import com.hansung.capstone.board.ResultRespond
import com.hansung.capstone.databinding.ActivityPostDetailBinding
import com.hansung.capstone.modify.ModifyComment
import com.hansung.capstone.modify.ModifyRecomment
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.item_post_detail_comments.*
import kotlinx.android.synthetic.main.item_post_detail_comments.view.*
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
        var scrapCheck:Int=0
        var buttonCheck: Int = 0
        var title_m=""
        var content_m=""
        var imageList_m= listOf<Int?>()
        private var instance: PostDetailActivity? = null
        fun getInstance(): PostDetailActivity? {
            return instance
        }
    }
    private val binding by lazy { ActivityPostDetailBinding.inflate(layoutInflater) }
    val api = CommunityService.create()
    lateinit var body: ResultGetPostDetail
    var commentActivity = 0
    var noImage = -1
    var commentId:Int=0
    var recommentId:Long=0
    var postId:Long=0
    var user_Id=MyApplication.prefs.getInt("userId",0)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        postId = intent.getIntExtra("id", 0).toLong()
        //MainActivity.getInstance()?.setPostIdCheck(postId)
        binding.imageButton.setOnClickListener {
            if (MyApplication.prefs.getString("accesstoken", "") != ""&&binding.InsertComment.text!=null) {
                val comment = binding.InsertComment.text.toString()
                binding.InsertComment.text = null
                softkeyboardHide()
                when(commentActivity){
                    0->PostComment(this@PostDetailActivity).postComment(comment, postId, binding)
                    1->PostReComment(this@PostDetailActivity).post(comment,postId, commentId,binding)
                    2-> ModifyComment().modify(commentId.toLong(),comment)
                    3-> ModifyRecomment().modify(recommentId.toLong(),comment)

                }

            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            commentActivity=0
        }
        getPostDetails(postId)
    }

    private fun getPostDetails(postId: Long) {
        api.getPostDetail(postId)
            .enqueue(object : Callback<ResultGetPostDetail> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResultGetPostDetail>,
                    response: Response<ResultGetPostDetail>,
                ) {
                    val body = response.body()
                    binding.PostTitle.text = body?.data?.title
                    title_m= body?.data?.title.toString()
                    binding.PostContent.text = body?.data?.content
                    content_m=body?.data?.content.toString()
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
                    var post_Id_=body.data.id
                    //var userId=MyApplication.prefs.getInt("userId",0)
                    if(body.data.authorId==user_Id){
                        binding.postActivity.isVisible=true}
                        binding.postActivity.setOnClickListener {
                        showDialog()
                    }
                    binding.CommentCount.text = count.toString()
                    var heartCount = body.data.postVoterId.size
                    var scrapCount=body.data.postScraperId.size
                    binding.HeartCount.text = heartCount.toString()
                    binding.StarCount.text=scrapCount.toString()
                    binding.BackToList.setOnClickListener {
                        finish()
                    }
                    scrapCheck = if (body.data.postScraperId.contains(user_Id.toLong())) {
                        binding.StarB.setImageResource(R.drawable.ic_star_check)
                        1
                    } else {
                        binding.StarB.setImageResource(R.drawable.ic_star_no_check)
                        0
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
                    binding.StarB.setOnClickListener {
                        api.checkScrap(user_Id.toLong(), postId.toLong())
                            .enqueue(object : Callback<ResultRespond> {
                                override fun onResponse(
                                    call: Call<ResultRespond>,
                                    response: Response<ResultRespond>,
                                ) {
                                    if (response.isSuccessful) {
                                        Log.d("checkScrap", "성공 : ${response.body().toString()}")
//                                    body?.data?.postVoterId?.let { it1 -> heartChange(it1) }
                                    }
                                }
                                override fun onFailure(call: Call<ResultRespond>, t: Throwable) {
                                    Log.d("checkScrap:", "실패 : $t")
                                }
                            })
                        runOnUiThread {
                            when (scrapCheck) {
                                0 -> {
                                    binding.StarB.setImageResource(R.drawable.ic_star_check)
                                    binding.StarCount.text = "${++scrapCount}"
                                    scrapCheck = 1
                                }
                                else -> {
                                    binding.StarB.setImageResource(R.drawable.ic_star_no_check)
                                    binding.StarCount.text = "${--scrapCount}"
                                    scrapCheck = 0
                                }
                            }
                        }
                    }
                    if (body.data.authorProfileImageId != noImage.toLong()) {
                        Glide.with(this@PostDetailActivity)
                            .load("${MyApplication.getUrl()}profile-image/${body.data.authorProfileImageId}") // 불러올 이미지 url
                            .override(100, 100)
                            .circleCrop() // 동그랗게 자르기
                            .into(binding.PostProfileImage) // 이미지를 넣을 뷰
                    } else binding.PostProfileImage.setImageResource(R.drawable.user)
                    // 이미지 등록
                    runOnUiThread {
                        if (body.data.imageId.isNotEmpty()) {
                            binding.ImageLayout.visibility = VISIBLE
                            val postImagesAdapter = PostDetailImagesAdapter(this@PostDetailActivity)
                            postImagesAdapter.imageList = body.data.imageId
                            imageList_m=body.data.imageId
                            binding.postImageRecyclerView.adapter = postImagesAdapter
                            postImageRecyclerView.addItemDecoration(
                                PostImageAdapterDecoration()
                            )
                        }else imageList_m= emptyList()
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
    }

    fun deletePost(){
        var accesstoken=MyApplication.prefs.getString("accesstoken","")
        //val myFragment = supportFragmentManager.findFragmentById(R.id.boardFragment) as BoardFragment?
        // Activity 클래스 내부

        api.deletePost(accessToken = "Bearer ${accesstoken}",user_Id.toLong(), postId)
            .enqueue(object : Callback<ResDelete> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResDelete>,
                    response: Response<ResDelete>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        if(body?.code==100) {
                            Log.d("INFO deletPost", "글삭제 성공" + body.toString())
                            // MyApplication.prefs.setInt("reloadCheck",1)
                            // (supportFragmentManager.findFragmentByTag("BOARD_FRAGMENT_TAG") as BoardFragment?)?.setreloadCheck(boolean = true)
                            MainActivity.getInstance()?.deleteCheck(true)
                            //deletePostCheck=true
                            //BoardFragment().initData()
                            finish()
                        }
                    }else {
                        //deletePostCheck=false
                        // 통신이 실패한 경우
                        Log.d("ERR deletePost", "onResponse 실패" + body?.toString())

                    }

                }
                override fun onFailure(call: Call<ResDelete>, t: Throwable) {
                    //deletePostCheck=false
                    Log.d("deltePost:", "실패 : $t")
                }
            })
        //if(deletePostCheck)
        //  Toast.makeText(this, "게시글이 삭제됐습니다", Toast.LENGTH_SHORT).show()
    }
    fun softkeyboardHide() {
        binding.InsertComment.clearFocus()
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    }
    fun postcomment(){
        PostComment(this@PostDetailActivity).postComments(postId, binding)
    }
    fun keyBordShow(int:Int) {
        commentActivity=int
        binding.InsertComment.clearFocus()
        binding.InsertComment.requestFocus()
        WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.ime())
    }

    fun goImageDetail(imageList: List<Int>, position: Int) {
        val intent = Intent(this, ImageFullScreenActivity::class.java)
        val intArr = imageList.toIntArray()
        intent.putExtra("imageList", intArr)
        intent.putExtra("position", position)
        startActivity(intent)
    }
    fun showDialog(){
        var dataArr=arrayOf("삭제하기","수정하기")
        val builder: AlertDialog.Builder= AlertDialog.Builder(this@PostDetailActivity)
        builder.setTitle("글 활동")
        var listener= DialogInterface.OnClickListener { dialog, which ->
            if(dataArr[which]=="삭제하기"){
                deletePost()
            }
            else if(dataArr[which]=="수정하기"){
                MainActivity.getInstance()?.setModifyCheck(true)
                MainActivity.getInstance()?.setModifyInform(title_m, content_m, imageList_m)
                val intent = Intent(this, WriteActivity::class.java)
                startActivity(intent)
                finish()
            }

        }
        builder.setItems(dataArr,listener)
        builder.setNegativeButton("취소",null)
        builder.show()
    }
}


