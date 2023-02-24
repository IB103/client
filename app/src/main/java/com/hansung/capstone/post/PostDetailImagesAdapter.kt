package com.hansung.capstone.post

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.CommunityService
import com.hansung.capstone.databinding.ItemPostDetailImagesBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostDetailImagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var imageList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemPostDetailImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = imageList[position]
        val viewHolder = holder as ImageHolder
        viewHolder.setImage(image, position)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ImageHolder(val binding: ItemPostDetailImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setImage(image: Int, position: Int) {
            val api = CommunityService.create()
            api.getImage(image.toLong()).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    Log.d("포스트 이미지", "성공 : ${response.body().toString()}")
                    val imageB = response.body()?.byteStream()
                    val bitmap = BitmapFactory.decodeStream(imageB)
                    binding.postImage.setImageBitmap(bitmap)
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.d("결과:", "실패 : $t")
                }
            })

            itemView.setOnClickListener {
                PostDetailActivity.getInstance()?.goImageDetail(imageList, position)
            }
        }
    }
}