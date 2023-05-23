package com.hansung.capstone.board


import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.MainActivity
import com.hansung.capstone.MyApplication
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemPostListBinding
import com.hansung.capstone.databinding.ItemPostListNoImageBinding

import java.time.format.DateTimeFormatter



const val post_type1 = 1
var noImage=-1

class BoardAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var resultGetPosts= mutableListOf<Posts?>()
    private var context:Context? = null
     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
         context = parent.context
        return when (viewType) {
            post_type1 -> {
                val binding =
                    ItemPostListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    BoardHolderType1(binding)
            }
           else-> {
                val binding = ItemPostListNoImageBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BoardHolderType2(binding)
            }

        }
    }
    override fun getItemViewType(position: Int):Int {
    //   if (resultGetPosts[position]!=null) {

            if (resultGetPosts[position]!!.imageId.isEmpty())
                resultGetPosts[position]!!.postType = 2
            else resultGetPosts[position]!!.postType = 1
       // }
        //else if(resultGetPosts[position]==null)
          //  resultGetPosts[position]!!.postType = 3
       // else if(resultGetPosts[position].id == -100)
           // resultGetPosts[position].postType = 3
        return resultGetPosts[position]!!.postType
    }
    override fun getItemCount(): Int {
        return this.resultGetPosts.size
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       // if(resultGetPosts[position].id!=-100){
            when (resultGetPosts[position]!!.imageId.size) {
                0 -> {
                    (holder as BoardHolderType2).bind(resultGetPosts[position]!!,position)
                }
                else -> {
                    (holder as BoardHolderType1).bind(resultGetPosts[position]!!,position)
                }

            }
        //}
//        if(holder is LoadingViewHolder){
//            holder.showLoadingView()
//        }
    }

    fun commentChanged(changedItem:Posts){

        val position=this.resultGetPosts.indexOf(changedItem)
        this.resultGetPosts[position]!!.commentChanged=true
        notifyItemChanged(position)
    }
    fun heartChanged(changedItem:Posts){
        Log.d("check#","2")
        val position=this.resultGetPosts.indexOf(changedItem)
        this.resultGetPosts[position]!!.heartChanged=true
        notifyItemChanged(position)
    }
    @SuppressLint("NotifyDataSetChanged")
    fun renewItems(resultGetPosts: ArrayList<Posts>){//위로 스크롤 땡길시 다시 page0 요청
        this.resultGetPosts.clear()
        this.resultGetPosts.addAll(resultGetPosts)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun setInitItems(resultGetPosts: ArrayList<Posts>){//초기 화면 세팅
        this.resultGetPosts.clear()
        this.resultGetPosts.addAll(resultGetPosts)
        notifyDataSetChanged()
    }
    fun removeAll(){//초기 화면 세팅
        this.resultGetPosts.clear()
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun moreItems(resultGetPosts: ArrayList<Posts>){//다음 페이지 요청
        this.resultGetPosts.addAll(resultGetPosts)
        notifyDataSetChanged()

    }
    inner class BoardHolderType1(private val binding: ItemPostListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Posts,position: Int) {
            items.commentList
            var count = 0
            val convertedDate = MyApplication.convertDate(items.createdDate)
            val createdDate = convertedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.PostUserName.text = items.nickname
            binding.BoardTitle.text = items.title
            binding.BoardContent.text = items.content
            binding.BoardDate.text = createdDate.toString()
            binding.ImageCount.text = items.imageId.size.toString()
            if(items.heartChanged){

                val d = Log.d("check#", "3")
                val size = items.postVoterId.size+1
                binding.HeartCount.text = size.toString()
                resultGetPosts[position]!!.heartChanged=false
            }else binding.HeartCount.text = items.postVoterId.size.toString()

            for (i in items.commentList) {
                count += i.reCommentList.size
            }
            for (i in 0 until items.commentList.size ) {
                val j:Long=-1
                if(items.commentList[i].userId!=j)
                    ++count
            }
            if(items.commentChanged){

                count+= MainActivity.getInstance()!!.getCommentCount()
                count-=  MainActivity.getInstance()!!.getDeletedCommentCount()
                //MainActivity.getInstance()!!.setCommentCount(-1)
                //MainActivity.getInstance()!!.setDeletedCommentCount(-1)
                resultGetPosts[position]!!.commentCount=count
                resultGetPosts[position]!!.commentChanged=false
            }
            binding.CommentCount.text = count.toString()
            Glide.with(context!!)
                .load("${MyApplication.getUrl()}image/${items.imageId[0]}") // 불러올 이미지 url
                .override(100, 100)
                .centerCrop()
                .into(binding.BoardImageView)
            if(items.authorProfileImageId!= noImage.toLong()){
                Glide.with(context!!)
                    .load("${MyApplication.getUrl()}profile-image/${items.authorProfileImageId}") // 불러올 이미지 url
                    .override(100, 100)
//                    .placeholder()
//                    .error(defaultImage)
//                    .fallback(defaultImage)
                    .circleCrop()
                    .into(binding.BoardProfileImage)
            }else binding.BoardProfileImage.setImageResource(R.drawable.user)
            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }
    inner class BoardHolderType2(private val binding: ItemPostListNoImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Posts,position: Int) {
           var count=0
            val convertedDate = MyApplication.convertDate(items.createdDate)
            val createdDate = convertedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.PostUserName.text = items.nickname
            binding.BoardTitle.text = items.title
            binding.BoardContent.text = items.content
            binding.BoardDate.text = createdDate.toString()
            if(items.heartChanged){
                var size: Int =0
                if(MainActivity.getInstance()!!.getHeartCheck()==1)
                    size = items.postVoterId.size+1
                else if(MainActivity.getInstance()!!.getHeartCheck()==0)
                    size= items.postVoterId.size-1
                binding.HeartCount.text = size.toString()
                resultGetPosts[position]!!.heartChanged=false
            }else binding.HeartCount.text = items.postVoterId.size.toString()
            for (i in items.commentList) {
                count += i.reCommentList.size
            }
            if(items.commentChanged){
                count+= MainActivity.getInstance()!!.getCommentCount()//추가한 댓글 더하기
                count-=  MainActivity.getInstance()!!.getDeletedCommentCount()
//                if(deleteCount!=0){//삭제한 댓글 카운트
//                    count-=deleteCount
//                }
               // MainActivity.getInstance()!!.setCommentCount(-1)
                //MainActivity.getInstance()!!.setDeletedCommentCount(-1)
                resultGetPosts[position]!!.commentChanged=false
            }


            for (i in 0 until items.commentList.size ) {
                val j:Long=-1
                if(items.commentList[i].userId!=j)
                    ++count
            }
            binding.CommentCount.text = count.toString()
            if(items.authorProfileImageId!= noImage.toLong()){
                Glide.with(context!!)
                    .load("${MyApplication.getUrl()}profile-image/${items.authorProfileImageId}") // 불러올 이미지 url
                    .override(100, 100)
                    .circleCrop()
                    .into(binding.BoardProfileImage)
            }else binding.BoardProfileImage.setImageResource(R.drawable.user)
            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }

}


