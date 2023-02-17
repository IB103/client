package com.hansung.capstone.map

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.R
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate


class SearchLocationAdapter(private val result: ResultSearchLocation) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_location_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return result.items.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val viewHolder = (holder as ViewHolder).itemView
//        viewHolder.findViewById<TextView>(R.id.result_title).text = result.items[position].title
//        viewHolder.findViewById<TextView>(R.id.result_roadAddress).text =
//            result.items[position].roadAddress
//        viewHolder.findViewById<TextView>(R.id.result_address).text = result.items[position].address
        val viewHolder = holder as ViewHolder
        viewHolder.bind(result.items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 연결
        val title = view.findViewById<TextView>(R.id.result_title)
        val roadAddress = view.findViewById<TextView>(R.id.result_roadAddress)
        val address = view.findViewById<TextView>(R.id.result_address)
        fun bind(items: Items) {
            items.title = items.title.replace("<b>", "")
            items.title = items.title.replace("</b>", "")
            title.text = items.title
            roadAddress.text = items.roadAddress
            address.text = items.address
            // 클릭 이벤트
            itemView.setOnClickListener {
                val tm = Tm128(items.mapx!!.toDouble(), items.mapy!!.toDouble())
                val cameraUpdate = CameraUpdate.scrollTo(tm.toLatLng())
                    .animate(CameraAnimation.Easing)
//                naverMap.moveCamera(cameraUpdate)
            }
        }
    }

}