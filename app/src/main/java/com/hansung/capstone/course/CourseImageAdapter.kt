package com.hansung.capstone.course

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.databinding.ItemCourseImagesBinding
import com.hansung.capstone.Waypoint

class CourseImageAdapter(
    var courseActivity: CourseActivity,
    var waypoints: List<Waypoint>
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
            courseActivity.openGallery(viewHolder,position)
//            val courseImageUri = courseActivity.openGallery(viewHolder,position)
//            viewHolder.binding.courseView.setImageURI(courseImageUri)
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

    //    @SuppressLint("SuspiciousIndentation")
    private fun addImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            } else {
//                ActivityCompat.requestPermissions(this,
//                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
//                    0)
//            }
//        }
    }

}