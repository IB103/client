package com.hansung.capstone.post

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
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
import com.hansung.capstone.modify.ModifyReComment
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
        var heartCheck: Int = 0
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
    private var commentActivity = 0
    var noImage = -1
    var commentId:Int=0
    var reCommentId:Long=0
    private var postId:Long=0
    var id=MyApplication.prefs.getLong("userId",0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        postId = intent.getLongExtra("postid", 0)
        binding.imageButton.setOnClickListener {
            if (MyApplication.prefs.getString("accessToken", "") != ""&&binding.InsertComment.text!=null) {
                val comment = binding.InsertComment.text.toString()
                binding.InsertComment.text = null
                softKeyboardHide()
                val commentAction = {
                    when(commentActivity){
                        0->PostComment(this@PostDetailActivity).postComment(comment, postId, binding)
                        1->PostReComment(this@PostDetailActivity).post(comment,postId, commentId.toLong(),binding)
                        2-> ModifyComment().modify(commentId.toLong(),comment)
                        3-> ModifyReComment().modify(reCommentId,comment)
                    }
                    commentActivity=0
                }
                if (Token().checkToken()) {
                    Token().issueNewToken(commentAction)
                }else{
                    commentAction()
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                intent.putExtra("loginNeeded",true)
                startActivity(intent)
            }

        }
        getPostDetails(postId)
    }

    private fun getPostDetails(postId: Long) {
        api.getPostDetail(postId)
            .enqueue(object : Callback<ResultGetPostDetail> {
                @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
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
                    for (i in 0 until body.data.commentList.size) {
                        val j:Long=-1
                        if(body.data.commentList[i].userId!=j)
                            ++count
                    }
                   // count += body.data.commentList.size
                    //var userId=MyApplication.prefs.getInt("userId",0)
                    if(body.data.authorId==id){
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


                    scrapCheck = if (body.data.postScraperId.contains(id)) {
                        binding.StarB.setImageResource(R.drawable.ic_star_check)
                        1
                    } else {
                        binding.StarB.setImageResource(R.drawable.ic_star_no_check)
                        0
                    }
//                    heartCheck = if (body.data.postScraperId.contains(user_Id.toLong())) {
//                        binding.heartB.setImageResource(R.drawable.ic_heart_check)
//                        1
//                    } else {
//                        binding.heartB.setImageResource(R.drawable.ic_heart_no_check)
//                        0
//                    }
                    // 좋아요 버튼
//                    buttonCheck = if (body.data.postVoterId.contains(user_Id.toLong())) {
                    heartCheck = if (body.data.postVoterId.contains(id)) {
                        binding.HeartB.setImageResource(R.drawable.ic_heart_check)
                        1
                    } else {
                        binding.HeartB.setImageResource(R.drawable.ic_heart_no_check)
                        0
                    }
                    binding.HeartB.setOnClickListener {
                        api.checkFavorite(id, postId)
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
                            when (heartCheck) {
                                0 -> {
                                    binding.HeartB.setImageResource(R.drawable.ic_heart_check)
                                    binding.HeartCount.text = "${++heartCount}"
                                    heartCheck = 1
                                }
                                else -> {
                                    binding.HeartB.setImageResource(R.drawable.ic_heart_no_check)
                                    binding.HeartCount.text = "${--heartCount}"
                                    heartCheck = 0
                                }
                            }
                        }
                    }
                    binding.StarB.setOnClickListener {
                        api.checkScrap(id, postId)
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
                            .circleCrop()
                            .into(binding.PostProfileImage)
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

    private fun deletePost(){
        val accessToken=MyApplication.prefs.getString("accessToken","")
        //val myFragment = supportFragmentManager.findFragmentById(R.id.boardFragment) as BoardFragment?
        // Activity 클래스 내부
        api.deletePost(accessToken = "Bearer $accessToken",id, postId)
            .enqueue(object : Callback<ResDelete> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<ResDelete>,
                    response: Response<ResDelete>,
                ) { val body = response.body()
                    if(response.isSuccessful){
                        if(body?.code==100) {
                            Log.d("INFO deletePost", "글삭제 성공 $body")
                            MainActivity.getInstance()?.stateCheck(0)
                            // MainActivity.getInstance()?.deleteCheck(true)
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
                    Log.d("deletePost:", "실패 : $t")
                }
            })
    }

     fun commentSuccess(int: Int){
         when(int){
             1-> Toast.makeText(this,"댓글 작성이 완료되었습니다.",Toast.LENGTH_SHORT).show()
             2-> Toast.makeText(this,"댓글 수정이 완료되었습니다.",Toast.LENGTH_SHORT).show()
             3-> Toast.makeText(this,"댓글 삭제가 완료되었습니다.",Toast.LENGTH_SHORT).show()
         }

    }
    private fun softKeyboardHide() {
        binding.InsertComment.clearFocus()
        WindowInsetsControllerCompat(window, window.decorView).hide(WindowInsetsCompat.Type.ime())
    }
    fun postComment(){
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
        val dataArr=arrayOf("삭제하기","수정하기")
        val builder: AlertDialog.Builder= AlertDialog.Builder(this@PostDetailActivity)
        builder.setTitle("글 활동")
        val listener= DialogInterface.OnClickListener { _, which ->
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


