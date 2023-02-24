package com.hansung.capstone.post

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CommunityService
import com.hansung.capstone.databinding.ItemImageDetailBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImageDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imageList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemImageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageDetailHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = imageList[position]
        val viewHolder = holder as ImageDetailHolder
        viewHolder.setImage(image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImageDetailHolder(val binding: ItemImageDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setImage(image: Int) {
            // 이미지 불러오기
            val api = CommunityService.create()
            api.getImage(image.toLong()).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("이미지", "성공 : ${response.body().toString()}")
                    val imageB = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(imageB)
                    binding.photoView.setImageBitmap(bitmap)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })
        }
    }
}