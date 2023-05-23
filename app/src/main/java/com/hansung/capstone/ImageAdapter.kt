package com.hansung.capstone

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
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
                removeImage(position)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            Glide.with(context)
                .load(uriList[position])
                .into(holder.image)

            holder.delete.setOnClickListener {
                removeImage(position)
            }
        //}
    }
    //수정할 사진 삭제
    private fun removeModifyImage(position: Int) {
        var string:String="${MyApplication.getUrl()}image/10"
        var url:Uri=string.toUri()
       // uriList.add(url)
        imageList.removeAt(position)
        if (imageList.size > 0) {
            binding.tvImageCount.text = imageList.size.toString()
            binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        } else {
            binding.tvImageCount.text = "0"
            binding.tvImageCount.setTextColor(context.getColor(R.color.gray))
        }
        context.removeImageId(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int =// if(modifyCheck) imageList.size
    //else
        uriList.size
    fun getItems() = uriList
    fun addItems(items:ArrayList<Uri>){//수정할 사진
        uriList.addAll(items)
        modifyCheck=true
        binding.tvImageCount.text = uriList.size.toString()
        binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        notifyDataSetChanged()
    }
    fun addItem(item: Uri) {//일반적으로 사진 선택시
        uriList.add(item)
        binding.tvImageCount.text = uriList.size.toString()
        binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        notifyDataSetChanged()
    }
    //수정할 사진 배치
//    fun setItem(item: List<Int>) {
//        imageList.addAll(item)
//        modifyCheck=true
//        binding.tvImageCount.text = imageList.size.toString()
//        binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
//        notifyDataSetChanged()
//    }
    //단순 게시글쓰기 액티비티에서 사진 배치
    private fun removeImage(position: Int) {
        context.removeImage(position,uriList[position])
        uriList.removeAt(position)
        if (uriList.size > 0) {
            binding.tvImageCount.text = uriList.size.toString()
            binding.tvImageCount.setTextColor(context.getColor(R.color.greenery))
        } else {
            binding.tvImageCount.text = "0"
            binding.tvImageCount.setTextColor(context.getColor(R.color.gray))
        }
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