package com.hansung.capstone.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemPostRecordBinding
import com.hansung.capstone.retrofit.RidingData
import java.time.format.DateTimeFormatter

class RecordAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var record: MutableList<RidingData> = mutableListOf()
    private var context: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val binding =
           ItemPostRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
           return RecordHolder(binding)

    }

    override fun getItemCount(): Int {
        return this.record.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as RecordAdapter.RecordHolder).bind(record[position])
    }
    fun addData(data:MutableList<RidingData>){
        this.record.clear()
        this.record.addAll(data)
        notifyDataSetChanged()
    }
    fun remove(){
        this.record.clear()
        notifyDataSetChanged()
    }

    inner class RecordHolder(private val binding: ItemPostRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: RidingData) {
            val convertedDate = items.createdDate.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
            binding.ridingDiestance.text = String.format("%.1fkm",items.ridingDistance )
            binding.createdDate.text=convertedDate
            val hours = (items.ridingTime / (1000 * 60 * 60)).toInt()
            val minutes = ((items.ridingTime / (1000 * 60)) % 60).toInt()
            binding.ridingTime.text=String.format("%dH%dM", hours, minutes)

    }
}
}