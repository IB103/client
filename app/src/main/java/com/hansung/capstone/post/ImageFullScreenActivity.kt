package com.hansung.capstone.post

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.hansung.capstone.MyApplication
import com.hansung.capstone.databinding.ActivityImageFullScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ImageFullScreenActivity : AppCompatActivity() {

    private val binding by lazy { ActivityImageFullScreenBinding.inflate(layoutInflater) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageArray = intent.getIntArrayExtra("imageList")
        val position = intent.getIntExtra("position", 0)
        val imageList = imageArray!!.toList()

        val imageDetailAdapter = ImageDetailAdapter(this@ImageFullScreenActivity,binding)
        imageDetailAdapter.imageList = imageList.toList()
        binding.fullScreenViewPager.adapter = imageDetailAdapter
        binding.fullScreenViewPager.setCurrentItem(position, false)
        binding.imagePage.text = "${position + 1} / ${imageList.size}"
        binding.ExitPhotoView.setOnClickListener {
            finish()
        }

        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.imagePage.text = "${position + 1} / ${imageList.size}"
            }
        })
    }
}