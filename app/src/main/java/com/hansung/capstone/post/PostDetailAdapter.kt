package com.hansung.capstone.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.board.Comments
import com.hansung.capstone.board.Posts
import com.hansung.capstone.R

class PostDetailAdapter(private val postDetail: Posts) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return postDetail.commentList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val viewHolder = (holder as ViewHolder).itemView
//        viewHolder.findViewById<TextView>(R.id.PostDetailCommentContent).text =
//            postDetail.commentList[position].content
        val viewHolder = holder as ViewHolder
        viewHolder.bind(postDetail.commentList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 연결
        private val content = view.findViewById<TextView>(R.id.PostDetailCommentContent)
        fun bind(items: Comments) {
            content.text = items.content
        }
    }
}