package com.hansung.capstone

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.databinding.ActivityWriteBinding

class ImageAdapter(private val context: WriteActivity, private val binding:ActivityWriteBinding ) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {
    private var uriList=ArrayList<Uri>()
    private var imageList=ArrayList<Int>()
    private var modifyCheck=false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view).listen { position, _ ->
            if(modifyCheck)
                removeModifyImage(position)
            else
                removeImage(position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(modifyCheck){
            val image = imageList[position]
            //var photo:Uri=MyApplication.getUrl()1
          //  uriList.add()
            Glide.with(context)
                .load("${MyApplication.getUrl()}image/${image}") // 불러올 이미지 url
               // .centerCrop()
                .into(holder.image)
            holder.delete.setOnClickListener {
                removeModifyImage(position)
            }
        }else {
            Glide.with(context)
                .load(uriList[position])
                .into(holder.image)

            holder.delete.setOnClickListener {
                removeImage(position)
            }
        }
    }
    //수정할 사진 삭제
    private fun removeModifyImage(position: Int) {
        imageList.removeAt(position)
        if (imageList.size > 0) {
            binding.tvImageCount.text = imageList.size.toString()
            binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        } else {
            binding.tvImageCount.text = "0"
            binding.tvImageCount.setTextColor(context.getColor(R.color.gray))
        }
        context.removeImage(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = if(modifyCheck) imageList.size
    else uriList.size
    fun getItems() = uriList

    fun addItem(item: Uri) {
        uriList.add(item)
        binding.tvImageCount.text = uriList.size.toString()
        binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        notifyDataSetChanged()
    }
    //수정할 사진 배치
    fun setItem(item: List<Int>) {
        imageList.addAll(item)
        modifyCheck=true
        binding.tvImageCount.text = imageList.size.toString()
        binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        notifyDataSetChanged()
    }
    //단순 게시글쓰기 액티비티에서 사진 배치
    private fun removeImage(position: Int) {
        uriList.removeAt(position)
        if (uriList.size > 0) {
            binding.tvImageCount.text = uriList.size.toString()
            binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        } else {
            binding.tvImageCount.text = "0"
            binding.tvImageCount.setTextColor(context.getColor(R.color.gray))
        }
        context.removeImage(position)
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val image: ImageView = view.findViewById(R.id.iv_image)
        val delete: ImageView = view.findViewById(R.id.iv_delete)

    }
    private fun <T: RecyclerView.ViewHolder> T.listen(event: (position: Int, type: Int) -> Unit): T {

        itemView.setOnClickListener {
       // event.invoke(adapterPosition, itemViewType)
        }
        return this
    }
}