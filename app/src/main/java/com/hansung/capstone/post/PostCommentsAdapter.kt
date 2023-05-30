package com.hansung.capstone.post

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.Token
import com.hansung.capstone.databinding.ItemPostDetailCommentsBinding
import com.hansung.capstone.delete.DeleteComment
import java.time.format.DateTimeFormatter


class PostCommentsAdapter(private val resultDetailPost: ResultGetPostDetail, private val context: PostDetailActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var noImage=-1
    var commentId:Long=0
    var totalComments=0
    var userId=MyApplication.prefs.getLong("userId",0)
    var accessToken=MyApplication.prefs.getString("accessToken","")
    override fun getItemCount(): Int {
        totalComments=resultDetailPost.data.commentList.count()

        return resultDetailPost.data.commentList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemPostDetailCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostCommentsHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as PostCommentsHolder
        viewHolder.bind(resultDetailPost.data.commentList[position])
    }

    inner class PostCommentsHolder(private val binding: ItemPostDetailCommentsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Comments) {
            if(items.userId.toInt()==-1){
                binding.CommentContent.text="삭제된 댓글입니다."
                binding.recommentBt.isVisible=false
                binding.CommentUserName.text=""
                binding.CommentProfileImage.setImageResource(R.drawable.user)
                binding.CommentCreatedDate.text = ""
            }else {
                binding.recommentBt.setOnClickListener {
                    context.commentId = items.id.toInt()
                    context.keyBordShow(1)
                }
//                binding.delelteComment.isVisible =
//                    items.userNickname==MyApplication.prefs.getString("nickname","")
                if(items.userNickname==MyApplication.prefs.getString("nickname","")){
                    binding.deleteComment.alpha = 1f
                    binding.deleteComment.isEnabled = true
                }else{
                    binding.deleteComment.alpha = 0.3f
                    binding.deleteComment.isEnabled = false
                }
                binding.deleteComment.setOnClickListener {
                    context.commentId= items.id.toInt()
                    commentId=items.id
                    showDialog()
                }
                binding.CommentContent.text = items.content
                binding.CommentUserName.text = items.userNickname
                val createdDate = MyApplication.convertDate(items.createdDate)
                    .format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
                binding.CommentCreatedDate.text = createdDate
                Log.d("imageID","${MyApplication.getUrl()}image/${items.userProfileImageId}")
                if (items.userProfileImageId != noImage.toLong()) {
                    Glide.with(context)
                        .load("${MyApplication.getUrl()}profile-image/${items.userProfileImageId}") // 불러올 이미지 url
//                        .override(200, 200)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .centerCrop()
                        .into(binding.CommentProfileImage)
                } else binding.CommentProfileImage.setImageResource(R.drawable.user)
            }
            if(items.reCommentList.isNotEmpty()) {
                totalComments+=items.reCommentList.size
                binding.PostDetailReComment.adapter = PostReCommentsAdapter(items,context)
            }
            else
                binding.PostDetailReComment.visibility= View.GONE

        }
    }
    private fun showDialog(){
        val dataArr=arrayOf("삭제하기","수정하기")
        val builder: AlertDialog.Builder= AlertDialog.Builder(context)
        builder.setTitle("댓글 활동")
        val listener= DialogInterface.OnClickListener { _, which ->
            if(dataArr[which]==dataArr[0]){
                if (Token().checkToken()) {
                    Token().issueNewToken{
                        DeleteComment().delete(userId,commentId)
                    }
                } else {
                    DeleteComment().delete(userId,commentId)
                }
            }
            else if(dataArr[which]==dataArr[1])
                context.keyBordShow(2)
        }
        builder.setItems(dataArr,listener)
        builder.setNegativeButton("취소",null)
        builder.show()
    }
}