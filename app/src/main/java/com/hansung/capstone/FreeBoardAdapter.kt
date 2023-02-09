package com.hansung.capstone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate

class FreeBoardAdapter(private val resultAllPost : ResultGetAllPost) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemCount(): Int {
        return  resultAllPost.data.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.freeboard_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var viewHolder = (holder as ViewHolder).itemView
        viewHolder.findViewById<TextView>(R.id.FreeBoardTitle).text = resultAllPost.data[position].title
        viewHolder.findViewById<TextView>(R.id.FreeBoardContent).text = resultAllPost.data[position].content
        viewHolder.findViewById<TextView>(R.id.FreeBoardDate).text = resultAllPost.data[position].createdDate
        holder.bind(resultAllPost.data[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 연결
        val title = view.findViewById<TextView>(R.id.FreeBoardTitle)
        val content = view.findViewById<TextView>(R.id.FreeBoardContent)
        val date = view.findViewById<TextView>(R.id.FreeBoardDate)
        fun bind(items: Posts) {
            title.text = items.title
            content.text = items.content
            date.text = items.createdDate
            itemView.setOnClickListener {
                FreeBoardActivity.getInstance()?.setFragment(items)
            }
        }
    }
}