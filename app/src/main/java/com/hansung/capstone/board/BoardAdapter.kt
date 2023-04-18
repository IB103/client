package com.hansung.capstone.board


import android.content.Context
import android.os.Handler
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


// 게시판에 들어갈 item type 설정
const val post_type1 = 1
const val post_type2 = 2
var noImage=-1
//const val post_type2 = 2
//const val post_type3 = 3
class BoardAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var DeleteCount=MyApplication.prefs.getInt("deleteCount",0)
    var isLoading=false
    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1
   // private var resultGetPosts=ArrayList<Posts>()
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
//            else->{
//
//                val binding = ItemPostListmoreLoadingBinding.inflate(
//                    LayoutInflater.from(parent.context),
//                    parent,
//                    false
//                )
//                LoadingViewHolder(binding)
//            }

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
    @Suppress("DEPRECATION")
    fun setLoadingView(b: Boolean) {
        if (b) {
            Handler().post{
                this.resultGetPosts.add(null)
                notifyItemInserted(this.resultGetPosts.size - 1)
            }
//                this.resultGetPosts.add(Posts(-100,"","","","",-100,"",-100, arrayListOf(), arrayListOf(),
//                    setOf(-100), setOf(-100),-100
//                ))
              // notifyDataSetChanged()
                //notifyItemInserted(this.resultGetPosts.size - 1)
        } else {
            if (this.resultGetPosts[resultGetPosts.size - 1] == null) {
                this.resultGetPosts.removeAt(resultGetPosts.size - 1)
                notifyItemRemoved(resultGetPosts.size)
            }
            //this.resultGetPosts.removeAt(this.resultGetPosts.size - 1)
      //  notifyDataSetChanged()
        // notifyItemRemoved(this.resultGetPosts.size)
        }
    }
    fun renewItems(newresultGetPosts: ArrayList<Posts>){//위로 스크롤 땡길시 다시 page0 요청
        this.resultGetPosts.clear()
        this.resultGetPosts.addAll(newresultGetPosts)
        notifyDataSetChanged()
    }
    fun setInitItems(newresultGetPosts: ArrayList<Posts>){//초기 화면 세팅
        this.resultGetPosts.clear()
        this.resultGetPosts.addAll(newresultGetPosts)
        notifyDataSetChanged()
    }
//    fun reLoad(newresultGetPosts: MutableList<Posts?>){//onresume 실행시
//       // val position=MainActivity.getInstance()?.getposition()
//        this.resultGetPosts.clear()
//        this.resultGetPosts.addAll(newresultGetPosts)
//        Log.d("size","${this.resultGetPosts.size}")
//        //notifyItemInserted(position!!)
//        notifyDataSetChanged()
//    }
fun reload(){
    var position=MainActivity.getInstance()?.getPosition()!!
    //notifyItemChanged(position)
    //  notifyItemInserted(position)
    notifyDataSetChanged()
}
    fun moreItems(newresultGetPosts: ArrayList<Posts>){//다음 페이지 요청
       // this.resultGetPosts.removeAt(this.resultGetPosts.size - 1)
       // notifyItemRemoved(resultGetPosts.size)
        //this.resultGetPosts.removeAt(this.resultGetPosts.lastIndex)
        this.resultGetPosts.addAll(newresultGetPosts)
//        this.resultGetPosts.add(Posts(-100,"","","","",-100,"",-100, arrayListOf(), arrayListOf(),
//            setOf(-100), setOf(-100),-100
//        ))

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
            binding.HeartCount.text = items.postVoterId.size.toString()
            for (i in items.commentList) {
                count += i.reCommentList.size
            }
            Log.d("count0","${count}")
            for (i in 0 until items.commentList.size ) {
                var j:Int=-1
                if(items.commentList[i].userId!=j.toLong())
                    ++count
            }
            Log.d("count1","${count}")
            //Log.d("postIdchecFk","${ MainActivity.getInstance()?.getPostIdCheck()}")
            if(MainActivity.getInstance()?.getPostIdCheck()==items.id.toLong()&&MainActivity.getInstance()?.getChangedPostCheck() == true){
               // Log.d("postId&commentcheck","################")
                Log.d("commentCount","${MyApplication.prefs.getInt("commentCount",0)}")
                count+= MyApplication.prefs.getInt("commentCount",0)
                Log.d("count2","${count}")
                Log.d("DeleteCount","${DeleteCount}")
                if(DeleteCount!=0){
                    count-= DeleteCount
                    Log.d("count3","${count}")
                }
                //MyApplication.prefs.removePostId()

                MainActivity.getInstance()?.setChangedPostCheck(false)
            }

            //count += items.commentList.size
            binding.CommentCount.text = count.toString()
            Glide.with(context!!)
                .load("${MyApplication.getUrl()}image/${items.imageId[0]}") // 불러올 이미지 url
                .override(100, 100)
                .centerCrop()
                .into(binding.BoardImageView) // 이미지를 넣을 뷰
            if(items.authorProfileImageId!= noImage.toLong()){
                Glide.with(context!!)
                    .load("${MyApplication.getUrl()}profile-image/${items.authorProfileImageId}") // 불러올 이미지 url
                    .override(100, 100)
//                    .placeholder() // 이미지 로딩 시작하기 전 표시할 이미지
//                    .error(defaultImage) // 로딩 에러 발생 시 표시할 이미지
//                    .fallback(defaultImage) // 로드할 url 이 비어있을(null 등) 경우 표시할 이미지
                    .circleCrop() // 동그랗게 자르기
                    .into(binding.BoardProfileImage) // 이미지를 넣을 뷰
            }else binding.BoardProfileImage.setImageResource(R.drawable.user)
            itemView.setOnClickListener {
                MainActivity.getInstance()?.setPosition(position)
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }
    inner class BoardHolderType2(private val binding: ItemPostListNoImageBinding,) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Posts,position: Int) {
           var count=0
            val convertedDate = MyApplication.convertDate(items.createdDate)
            val createdDate = convertedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.PostUserName.text = items.nickname
            binding.BoardTitle.text = items.title
            binding.BoardContent.text = items.content
            binding.BoardDate.text = createdDate.toString()
            binding.HeartCount.text = items.postVoterId.size.toString()
            for (i in items.commentList) {
                count += i.reCommentList.size
            }
            Log.d("postIdchecFk","${ MainActivity.getInstance()?.getPostIdCheck()}")
            if(MainActivity.getInstance()?.getPostIdCheck()==items.id.toLong()&&MainActivity.getInstance()?.getChangedPostCheck() == true){
                Log.d("commentCount#####","${MyApplication.prefs.getInt("commentCount",0)}")
                count+= MyApplication.prefs.getInt("commentCount",0)//추가한 댓글 더하기
                Log.d("count1","${count}")
                if(DeleteCount!=0){//삭제한 댓글 카운트
                    Log.d("deletecount##","${DeleteCount}")
                    count-= DeleteCount
                   // MyApplication.prefs.removeDeletedCount()
                }
                //MyApplication.prefs.removePostId()
               // MyApplication.prefs.removeCommentCount()
                MainActivity.getInstance()?.setChangedPostCheck(false)
            }
            for (i in 0 until items.commentList.size ) {
                var j:Int=-1
                if(items.commentList[i].userId!=j.toLong())
                    ++count
            }
           // count += items.commentList.size
            binding.CommentCount.text = count.toString()
            if(items.authorProfileImageId!= noImage.toLong()){
                Glide.with(context!!)
                    .load("${MyApplication.getUrl()}profile-image/${items.authorProfileImageId}") // 불러올 이미지 url
                    .override(100, 100)
                    .circleCrop() // 동그랗게 자르기
                    .into(binding.BoardProfileImage) // 이미지를 넣을 뷰
            }else binding.BoardProfileImage.setImageResource(R.drawable.user)
            itemView.setOnClickListener {
                MainActivity.getInstance()?.setPosition(position)
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }

}


