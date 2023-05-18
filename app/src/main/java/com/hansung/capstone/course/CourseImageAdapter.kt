package com.hansung.capstone.course

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.CustomDialog
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemCourseImagesBinding
import com.hansung.capstone.Waypoint
import com.hansung.capstone.map.WaypointsSearchActivity

class CourseImageAdapter(
    var courseActivity: CourseActivity,
    var waypoints: MutableList<Waypoint>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemCourseImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CourseImageHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CourseImageAdapter.CourseImageHolder
        viewHolder.bind(waypoints[position])
        viewHolder.binding.imageNum.text = (position + 1).toString()
        viewHolder.binding.courseImage.setOnClickListener {
            courseActivity.openGallery(viewHolder, position)
        }
        viewHolder.binding.coursePlaceName.setOnClickListener {
            openCustomDialog(
                viewHolder.binding.coursePlaceName.text.toString(),
                position,
                courseActivity,
                this
            )
        }
        viewHolder.binding.searchButton.setOnClickListener {
            val intent = Intent(courseActivity, WaypointsSearchActivity::class.java)
            intent.putExtra("position", viewHolder.adapterPosition)
            courseActivity.searchLauncher.launch(intent)
        }
    }

    override fun getItemCount(): Int {
        return waypoints.count()
    }

    inner class CourseImageHolder(val binding: ItemCourseImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isImageSet = false
        fun bind(items: Waypoint) {
            binding.coursePlaceName.text = items.place_name
            if (!isImageSet) {
                Glide.with(courseActivity)
                    .load(R.drawable.add_image) // 이미지 리소스 ID
                    .centerCrop()
                    .into(binding.courseView)

                isImageSet = true
            }
        }
    }

    private fun updateItem(position: Int, placeName: String) {
        val updatedItem = waypoints[position].copy(place_name = placeName)
        waypoints[position] = updatedItem
        notifyItemChanged(position, updatedItem)
    }

    fun updateItemBySearch(position: Int, placeName: String, placeUrl: String) {
        val updatedItem = waypoints[position].copy(place_name = placeName, place_url = placeUrl)
        waypoints[position] = updatedItem
        notifyItemChanged(position, updatedItem)
    }

    private fun openCustomDialog(
        preName: String,
        pos: Int,
        courseActivity: CourseActivity,
        adapter: CourseImageAdapter
    ) {
        val customDialog = CustomDialog(courseActivity)

        val searchBox = customDialog.findViewById<EditText>(R.id.placeName) // 다이얼로그 내 EditText 찾기
        val enrollButton = customDialog.findViewById<Button>(R.id.enrollButton)
        val cancelButton = customDialog.findViewById<Button>(R.id.cancelButton)

        if (preName.isNotEmpty())
            searchBox.setText(preName)
        searchBox.requestFocus()

        customDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        customDialog.show()

        enrollButton.setOnClickListener {
            customDialog.dismiss()
            adapter.updateItem(pos, searchBox.text.toString())
        }

        cancelButton?.setOnClickListener {
            customDialog.dismiss()
        }
    }

}