package com.hansung.capstone.map

import android.app.Activity
import android.content.Intent
//import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemWaypointSearchResultRecyclerviewBinding

class WaypointsSearchAdapter(
    val waypointsSearchActivity: WaypointsSearchActivity,
    private val resultSearchKeyword: ResultSearchKeyword
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemWaypointSearchResultRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return WaypointsSearchHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as WaypointsSearchAdapter.WaypointsSearchHolder
        viewHolder.bind(resultSearchKeyword.documents[position])
    }

    override fun getItemCount(): Int {
        return resultSearchKeyword.documents.count()
    }

    inner class WaypointsSearchHolder(private val binding: ItemWaypointSearchResultRecyclerviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: Place) {
            binding.placeName.text = items.place_name
            binding.addressName.text = items.address_name
            itemView.setOnClickListener {
                val position = waypointsSearchActivity.intent.getIntExtra("position", -1)
//                Log.d("포지션", position.toString())
                // "전달할 값"이라는 문자열을 포함하는 Intent 생성
                val resultIntent = Intent()
                resultIntent.putExtra("position", position)
                resultIntent.putExtra("place_name", items.place_name)
                resultIntent.putExtra("place_lat", items.y)
                resultIntent.putExtra("place_lng", items.x)
                resultIntent.putExtra("place_url", items.place_url)

                // setResult()를 사용하여 결과 데이터 설정
                waypointsSearchActivity.setResult(Activity.RESULT_OK, resultIntent)

                // 액티비티 종료
                waypointsSearchActivity.finish()
                waypointsSearchActivity.overridePendingTransition(0, R.anim.slide_out_right)
            }
        }
    }
}