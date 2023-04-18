package com.hansung.capstone.post

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityImageFullScreenBinding
import com.hansung.capstone.databinding.ItemImageDetailBinding
import kotlinx.coroutines.*
import java.util.*

class ImageDetailAdapter(
    private val context: Context,
    val binding: ActivityImageFullScreenBinding
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imageList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemImageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageDetailHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = imageList[position]
        val viewHolder = holder as ImageDetailHolder
        viewHolder.setImage(image)

        binding.DownloadButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressImage.visibility = View.VISIBLE
                val fileName = "${System.currentTimeMillis()}.jpg"
//                Log.d("이름", fileName)
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                Log.d("이미지", image.toString())
                    Glide.with(context)
                        .asBitmap()
                        .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                // Bitmap을 파일로 저장
                                val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                Log.d("uri",uri.toString())
                                uri?.let {
                                    val outputStream = resolver.openOutputStream(uri)
                                    resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                    outputStream?.close()
                                }
                                binding.progressImage.visibility = View.GONE
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                // 호출된 이미지 로딩 작업이 취소되면 호출
                            }
                        })
            }
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }


    inner class ImageDetailHolder(val binding: ItemImageDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setImage(image: Int) {
            // 이미지 불러오기
            Log.d("dd", image.toString())
            Glide.with(context)
                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
                .into(binding.photoView) // 이미지를 넣을 뷰
        }

    }
}