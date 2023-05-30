package com.hansung.capstone.course

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.text.InputType
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ItemCourseImagesBinding
import com.hansung.capstone.Waypoint
import com.hansung.capstone.map.WaypointsSearchActivity

class CourseImageAdapter(
    var courseActivity: CourseActivity,
    var waypoints: MutableList<Waypoint>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemCourseImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CourseImageHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as CourseImageAdapter.CourseImageHolder
        viewHolder.bind(waypoints[position])
        viewHolder.binding.imageNum.text = (position + 1).toString()
        viewHolder.binding.courseImage.setOnClickListener {
            courseActivity.openGallery(viewHolder, position)
        }
        when (courseActivity.modeSet) {
            1 -> {
                viewHolder.binding.coursePlaceName.setOnClickListener {
                    courseDialog(
                        courseActivity,
                        viewHolder.binding.coursePlaceName.text.toString(),
                        position,
                        this
                    )
                }
                viewHolder.binding.searchButton.setOnClickListener {
                    val intent = Intent(courseActivity, WaypointsSearchActivity::class.java)
                    intent.putExtra("position", viewHolder.adapterPosition)
                    courseActivity.searchLauncher.launch(intent)
                }
            }
            2 -> {
                viewHolder.binding.searchButton.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return waypoints.count()
    }

    inner class CourseImageHolder(val binding: ItemCourseImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private var isImageSet = false
        fun bind(items: Waypoint) {
            binding.coursePlaceName.text = items.place_name
            if (!isImageSet) {
                Glide.with(courseActivity)
                    .load(R.drawable.add_image) // 이미지 리소스 ID
                    .centerCrop()
                    .into(binding.courseView)

                isImageSet = true
            }
        }
    }

    private fun updateItemByInput(position: Int, placeName: String) {
        val updatedItem = waypoints[position].copy(place_name = placeName, place_url = "")
        waypoints[position] = updatedItem
        notifyItemChanged(position, updatedItem)
    }

    fun updateItemBySearch(position: Int, placeName: String, placeUrl: String) {
        val updatedItem = waypoints[position].copy(place_name = placeName, place_url = placeUrl)
        waypoints[position] = updatedItem
        notifyItemChanged(position, updatedItem)
    }

    private fun courseDialog(
        context: Context,
        preName: String,
        pos: Int,
        adapter: CourseImageAdapter
    ) {
        val alertDialog = AlertDialog.Builder(context)
            .setMessage("장소명 등록")

        val editText = EditText(context)
        alertDialog.setView(editText)
        if (preName.isNotEmpty())
            editText.setText(preName)
        editText.setBackgroundResource(R.drawable.element_edit_box2)
        editText.hint = "장소명을 입력해주세요."
        editText.inputType = InputType.TYPE_CLASS_TEXT
        val textSizeInSp = 16f
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSizeInSp)
        editText.setPadding(
            convertDpToPx(8),
            0,
            convertDpToPx(8),
            0
        )
        if (preName.isNotEmpty())
            editText.setText(preName)

        val container = FrameLayout(context)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            convertDpToPx(50)
        )
        val horizontalMarginInDp = 16
        val verticalMarginInDp = 0
        layoutParams.setMargins(
            convertDpToPx(horizontalMarginInDp),
            convertDpToPx(verticalMarginInDp),
            convertDpToPx(horizontalMarginInDp),
            convertDpToPx(verticalMarginInDp)
        )
        container.addView(editText, layoutParams)

        alertDialog.setView(container)

        alertDialog.setPositiveButton("등록") { dialog, _ ->
            dialog.dismiss()
            adapter.updateItemByInput(pos, editText.text.toString())
        }
        alertDialog.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        editText.requestFocus()
        val window = alertDialog.show().window
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    private fun convertDpToPx(dp: Int): Int {
        val scale = Resources.getSystem().displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

}