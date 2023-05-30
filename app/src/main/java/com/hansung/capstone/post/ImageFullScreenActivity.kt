package com.hansung.capstone.post

import android.annotation.SuppressLint
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityImageFullScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ImageFullScreenActivity : AppCompatActivity() {

    private val binding by lazy { ActivityImageFullScreenBinding.inflate(layoutInflater) }
    private var selectedPosition: Int = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageArray = intent.getIntArrayExtra("imageList")
        selectedPosition = intent.getIntExtra("position", 0)
        val imageList = imageArray!!.toList()

        val imageDetailAdapter = ImageDetailAdapter(this@ImageFullScreenActivity,binding)
        imageDetailAdapter.imageList = imageList.toList()
        binding.fullScreenViewPager.adapter = imageDetailAdapter
        binding.fullScreenViewPager.setCurrentItem(selectedPosition, false)
        binding.imagePage.text = "${selectedPosition + 1} / ${imageList.size}"
        binding.ExitPhotoView.setOnClickListener {
            finish()
        }

        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @RequiresApi(Build.VERSION_CODES.Q)
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                selectedPosition = position
                binding.imagePage.text = "${position + 1} / ${imageList.size}"
            }
        })

        binding.DownloadButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                binding.progressImage.visibility = View.VISIBLE
                val fileName = "${System.currentTimeMillis()}.jpg"
                val resolver = this@ImageFullScreenActivity.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                Glide.with(this@ImageFullScreenActivity)
                    .asBitmap()
                    .load("${MyApplication.getUrl()}image/${imageList[selectedPosition]}") // 불러올 이미지 url
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val uri = resolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )
                            uri?.let {
                                val outputStream = resolver.openOutputStream(uri)
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                outputStream?.close()
                            }
                            binding.progressImage.visibility = View.GONE
                            Toast.makeText(this@ImageFullScreenActivity, "사진이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // 호출된 이미지 로딩 작업이 취소되면 호출
                        }
                    })
            }
        }
    }
}