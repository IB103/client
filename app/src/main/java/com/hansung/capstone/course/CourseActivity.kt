package com.hansung.capstone.course

import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.hansung.capstone.R
import com.hansung.capstone.databinding.ActivityCourseBinding



class CourseActivity:AppCompatActivity() {
    private val binding by lazy { ActivityCourseBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
//        val layout = findViewById<ConstraintLayout>(R.id.courseImageView) // 레이아웃 ID로 레이아웃 객체를 가져옴
//        val params = layout.layoutParams // 레이아웃의 파라미터 가져옴
//        params.height = ViewGroup.LayoutParams.MATCH_PARENT // height를 match_parent로 설정
//        params.width = layout.width // width를 레이아웃의 넓이와 같은 값으로 설정
//        layout.layoutParams = params

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val focusView = currentFocus
        if (focusView != null && ev != null) {
            val rect = Rect()
            focusView.getGlobalVisibleRect(rect)
            val x = ev.x.toInt()
            val y = ev.y.toInt()

            if (!rect.contains(x, y)) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(focusView.windowToken, 0)
                focusView.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}