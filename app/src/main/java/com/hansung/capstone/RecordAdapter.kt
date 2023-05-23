package com.hansung.capstone

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.board.BoardAdapter
import com.hansung.capstone.board.Posts
import com.hansung.capstone.databinding.ItemPostListBinding
import com.hansung.capstone.retrofit.RidingData

class RecordAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>()  {
    private var context: Context? = null
    private var record= mutableListOf<RidingData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       context=parent.context
        val binding=   ItemPostListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecordHolder(binding)
    }

    override fun getItemCount(): Int {
        return this.record.size
    }
    fun setData(resultGetPosts: List<RidingData>){//초기 화면 세팅
        this.record.clear()
        this.record.addAll(resultGetPosts)
        notifyDataSetChanged()
    }
    fun removeAll(){//초기 화면 세팅
        this.record.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecordAdapter.RecordHolder).bind(record[position],position)
    }
    inner class RecordHolder(private val binding: ItemPostListBinding):
        RecyclerView.ViewHolder(binding.root){
            fun bind(items:RidingData,position: Int){

            }
        }
}