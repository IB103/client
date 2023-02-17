package com.hansung.capstone.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.MainActivity
import com.hansung.capstone.R

class BoardAdapter(private val resultAllPost: ResultGetAllPost) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.board_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return resultAllPost.data.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val viewHolder = (holder as ViewHolder).itemView
//        viewHolder.findViewById<TextView>(R.id.BoardTitle).text =
//            resultAllPost.data[position].title
//        viewHolder.findViewById<TextView>(R.id.BoardContent).text =
//            resultAllPost.data[position].content
//        viewHolder.findViewById<TextView>(R.id.BoardDate).text =
//            resultAllPost.data[position].createdDate
        val viewHolder = (holder as ViewHolder)
        viewHolder.bind(resultAllPost.data[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.BoardTitle)
        private val content = view.findViewById<TextView>(R.id.BoardContent)
        private val date = view.findViewById<TextView>(R.id.BoardDate)
        fun bind(items: Posts) {
            title.text = items.title
            content.text = items.content
            date.text = items.createdDate
            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }
        }
    }
}