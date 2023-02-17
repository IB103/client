package com.hansung.capstone.board

import android.graphics.BitmapFactory
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.internal.LifecycleCallback.getFragment
import com.hansung.capstone.BoardFragment
import com.hansung.capstone.MainActivity
import com.hansung.capstone.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class BoardAdapter(private val resultAllPost: ResultGetAllPost) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val server_info = "121.138.93.178:9999"
        private const val url = "http://$server_info/"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.board_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return resultAllPost.data.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val viewHolder = (holder as ViewHolder).itemView
//        viewHolder.findViewById<TextView>(R.id.BoardTitle).text =
//            resultAllPost.data[position].title
//        viewHolder.findViewById<TextView>(R.id.BoardContent).text =
//            resultAllPost.data[position].content
//        viewHolder.findViewById<TextView>(R.id.BoardDate).text =
//            resultAllPost.data[position].createdDate
        val viewHolder = (holder as ViewHolder)
        viewHolder.bind(resultAllPost.data[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.BoardTitle)
        private val content = view.findViewById<TextView>(R.id.BoardContent)
        private val date = view.findViewById<TextView>(R.id.BoardDate)
        private val image = view.findViewById<ImageView>(R.id.BoardImageView)
        fun bind(items: Posts) {
            title.text = items.title
            content.text = items.content
            date.text = items.createdDate
            if (items.imageId.isNotEmpty()) {
                Log.d("이미지 아이디","${items.imageId[0]}")
                // 이미지 불러오기
                val retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build()

                val api = retrofit.create(GetAllPostImageInterface::class.java)
                api.getImage(items.imageId[0].toLong()).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Log.d("결과", "성공 : ${response.body().toString()}")
                        val imageB = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(imageB)
                        image.setImageBitmap(bitmap)
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.d("결과:", "실패 : $t")
                    }
                })
            }

            itemView.setOnClickListener {
                MainActivity.getInstance()?.goPostDetail(items)
            }

        }
    }
}