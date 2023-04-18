package com.hansung.capstone.post

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CommunityService
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemPostDetailRecommentsBinding
import com.hansung.capstone.delete.DeleteRecomment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.format.DateTimeFormatter

class PostReCommentsAdapter(private val comment: Comments,private val context: PostDetailActivity) :

    RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    val noImage=-1
    var recommentId:Long=0
    var accesstoken=MyApplication.prefs.getString("accesstoken","")
    var userId=MyApplication.prefs.getInt("userId",0)
    var DeleteCount=MyApplication.prefs.getInt("deleteCount",0)
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
                context.recommentId= items.id
                recommentId=items.id
                showDialog()
            }
            val api = CommunityService.create()
            Log.d("userProfileImageId","${items.userProfileImageId}")
            if(items.userProfileImageId==noImage.toLong()){
                binding.reCommentProfileImage.setImageResource(R.drawable.user)
            }
            else{ api.getProfileImage(items.userProfileImageId).enqueue(object :
                Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("결과", "성공 : ${response.body().toString()}")
                    val imageB = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(imageB)
                    binding.reCommentProfileImage.setImageBitmap(bitmap)

                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })}

        }
    }
    private fun showDialog(){
        var dataArr=arrayOf("삭제하기","수정하기")
        val builder: AlertDialog.Builder= AlertDialog.Builder(context)
        builder.setTitle("댓글 활동")
        var listener= DialogInterface.OnClickListener { dialog, which ->
            if(dataArr[which]==dataArr[0]){
                if(comment.id.toInt()==-1)
                    MyApplication.prefs.setInt("deleteCount",++DeleteCount)
                MainActivity.getInstance()?.setChangedPostCheck(true)
                DeleteRecomment().delete(accesstoken, userId, recommentId)
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