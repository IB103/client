package com.hansung.capstone.post

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.hansung.capstone.databinding.ActivityImageFullScreenBinding

class ImageFullScreenActivity : AppCompatActivity() {

    private val binding by lazy { ActivityImageFullScreenBinding.inflate(layoutInflater) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val imageArray = intent.getIntArrayExtra("imageList")
        val position = intent.getIntExtra("position", 0)
        val imageList = imageArray!!.toList()

        val imageDetailAdapter = ImageDetailAdapter()
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