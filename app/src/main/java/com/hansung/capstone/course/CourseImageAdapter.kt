package com.hansung.capstone.course

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CustomDialog
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemCourseImagesBinding
import com.hansung.capstone.Waypoint
import com.hansung.capstone.map.WaypointsSearchActivity

class CourseImageAdapter(
    var courseActivity: CourseActivity,
    var waypoints: MutableList<Waypoint>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

//    private var selectedImageUri: Uri? = null
//    val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//        if (result.resultCode == AppCompatActivity.RESULT_OK) {
//            val selectedImageUri: Uri? = result.data?.data
////                val data = result.data?.data
////                imageView.setImageURI(data)
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemCourseImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CourseImageHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CourseImageAdapter.CourseImageHolder
        viewHolder.bind(waypoints[position])
        viewHolder.binding.courseImage.setOnClickListener {
            courseActivity.openGallery(viewHolder, position)
//            val courseImageUri = courseActivity.openGallery(viewHolder,position)
//            viewHolder.binding.courseView.setImageURI(courseImageUri)
        }
        viewHolder.binding.coursePlaceName.setOnClickListener {
            openCustomDialog(position, courseActivity, this)
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
        fun bind(items: Waypoint) {
            binding.coursePlaceName.text = items.place_name
        }
    }

    //    fun updateItem(position: Int, placeName: String, placeUrl: String) {
    private fun updateItem(position: Int, placeName: String) {
        val updatedItem = waypoints[position].copy(place_name = placeName)
//        waypoints = waypoints.toMutableList().apply {
//            set(position, updatedItem)
//        }
        waypoints[position] = updatedItem
        notifyItemChanged(position, updatedItem)
//        waypoints[position].place_name = placeName
//        notifyItemChanged(position)
    }

    fun updateItemBySearch(position: Int, placeName: String, placeUrl: String) {
        val updatedItem = waypoints[position].copy(place_name = placeName, place_url = placeUrl)
//        waypoints = waypoints.toMutableList().apply {
//            set(position, updatedItem)
//        }
        waypoints[position] = updatedItem
        notifyItemChanged(position, updatedItem)
//        waypoints[position].place_name = placeName
//        waypoints[position].place_url = placeUrl
//        notifyItemChanged(position)
    }
    //    @SuppressLint("SuspiciousIndentation")
//    private fun addImage() {
//        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
////        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
////            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
////            } else {
////                ActivityCompat.requestPermissions(this,
////                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
////                    0)
////            }
////        }
//    }

    private fun openCustomDialog(
        pos: Int,
        courseActivity: CourseActivity,
        adapter: CourseImageAdapter
    ) {
        val customDialog = CustomDialog(courseActivity)
        customDialog.show()

        val searchBox = customDialog.findViewById<EditText>(R.id.placeName) // 다이얼로그 내 EditText 찾기
        val enrollButton = customDialog.findViewById<Button>(R.id.enrollButton)
        val cancelButton = customDialog.findViewById<Button>(R.id.cancelButton)


        enrollButton.setOnClickListener {
            customDialog.dismiss()
            adapter.updateItem(pos, searchBox.text.toString())
        }

        cancelButton?.setOnClickListener {
            customDialog.dismiss()
        }
    }

}