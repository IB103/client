package com.hansung.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostDetailAdapter(private val postDetail: Posts) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return postDetail.commentList.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view =
            LayoutInflater.from(parent.context).inflate(R.layout.comment_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = (holder as ViewHolder).itemView
        viewHolder.findViewById<TextView>(R.id.PostDetailCommentContent).text =
            postDetail.commentList[position].content
        holder.bind(postDetail.commentList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 연결
        private val content = view.findViewById<TextView>(R.id.PostDetailCommentContent)
        fun bind(items: Comments) {
            content.text = items.content
        }
    }
}