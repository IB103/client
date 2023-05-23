package com.hansung.capstone.post

import android.app.AlertDialog
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemPostDetailRecommentsBinding
import com.hansung.capstone.delete.DeleteReComment
import java.time.format.DateTimeFormatter

class PostReCommentsAdapter(private val comment: Comments,private val context: PostDetailActivity) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    val noImage:Long=-1
    var reCommentId:Long=0
    var accessToken=MyApplication.prefs.getString("accessToken","")
    var userId=MyApplication.prefs.getLong("userId",0)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPostDetailRecommentsBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostReCommentsHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as PostReCommentsHolder
        viewHolder.bind(comment.reCommentList[position])
    }

    override fun getItemCount(): Int {
        return comment.reCommentList.count()
    }

    inner class PostReCommentsHolder(private val binding: ItemPostDetailRecommentsBinding) :
        RecyclerView.ViewHolder(binding.root){
        fun bind(items: ReComments){
            binding.reCommentContent.text = items.content
            binding.reCommentUserName.text = items.userNickname
            val createdDate = MyApplication.convertDate(items.createdDate).format(
                DateTimeFormatter.ofPattern("MM/dd HH:mm"))
            binding.reCommentCreatedDate.text = createdDate
            binding.delelteReComment.isVisible =
                items.userNickname==MyApplication.prefs.getString("nickname","")
            binding.delelteReComment.setOnClickListener {
                context.reCommentId= items.id
                reCommentId=items.id
                showDialog()
            }

            Log.d("userProfileImageId","${items.userProfileImageId}")
            if(items.userProfileImageId==noImage.toLong()){
                binding.reCommentProfileImage.setImageResource(R.drawable.user)
            }
            else{
                if (items.userProfileImageId != noImage.toLong()) {
                    Glide.with(context)
                        .load("${MyApplication.getUrl()}profile-image/${items.userProfileImageId}")
//                        .override(200, 200)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .centerCrop()
                        .into(binding.reCommentProfileImage)

                }
            }

        }
    }
    private fun showDialog(){
        val dataArr=arrayOf("삭제하기","수정하기")
        val builder: AlertDialog.Builder= AlertDialog.Builder(context)
        builder.setTitle("댓글 활동")
        val listener= DialogInterface.OnClickListener { _, which ->
            if(dataArr[which]==dataArr[0]){
                DeleteReComment().delete(accessToken, userId, reCommentId)
            }
            else if(dataArr[which]==dataArr[1]){
                context.keyBordShow(3)
            }
        }
        builder.setItems(dataArr,listener)
        builder.setNegativeButton("취소",null)
        builder.show()
    }
}