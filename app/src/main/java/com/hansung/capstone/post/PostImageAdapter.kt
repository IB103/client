package com.hansung.capstone.post

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hansung.capstone.board.GetAllPostImageInterface
import com.hansung.capstone.databinding.ItemPostImagesBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class PostImageAdapter : RecyclerView.Adapter<ImageHolder>(){
    var imageList = listOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val binding = ItemPostImagesBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ImageHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        val image = imageList[position]
        holder.setImage(image)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }
}

class ImageHolder(val binding: ItemPostImagesBinding):RecyclerView.ViewHolder(binding.root){

    companion object {
//        private const val server_info = "121.138.93.178:9999"
        private const val server_info = "223.194.133.220:8080"
        private const val url = "http://$server_info/"
    }

    fun setImage(image: Int){
        // 이미지 불러오기
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()

        val api = retrofit.create(GetAllPostImageInterface::class.java)
        api.getImage(image.toLong()).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                Log.d("결과", "성공 : ${response.body().toString()}")
                val imageB = response.body()?.byteStream()
                val bitmap = BitmapFactory.decodeStream(imageB)
                binding.postImage.setImageBitmap(bitmap)
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("결과:", "실패 : $t")
            }
        })
    }
}
