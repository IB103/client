package com.hansung.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.format.DateTimeFormatter

class FreeBoardAdapter(private val resultAllPost: ResultGetAllPost) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return resultAllPost.data.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.freeboard_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = (holder as ViewHolder).itemView
        viewHolder.findViewById<TextView>(R.id.FreeBoardTitle).text =
            resultAllPost.data[position].title
        viewHolder.findViewById<TextView>(R.id.FreeBoardContent).text =
            resultAllPost.data[position].content
        viewHolder.findViewById<TextView>(R.id.FreeBoardDate).text =
            resultAllPost.data[position].createdDate.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
        holder.bind(resultAllPost.data[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.FreeBoardTitle)
        private val content = view.findViewById<TextView>(R.id.FreeBoardContent)
        private val date = view.findViewById<TextView>(R.id.FreeBoardDate)
        fun bind(items: Posts) {
            title.text = items.title
            content.text = items.content
            date.text = items.createdDate.format(DateTimeFormatter.ofPattern("MM/dd HH:mm"))
            itemView.setOnClickListener {
                FreeBoardActivity.getInstance()?.goPostDetail(items)
            }
        }
    }
}