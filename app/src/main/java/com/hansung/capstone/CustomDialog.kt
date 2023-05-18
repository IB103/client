package com.hansung.capstone

import android.app.Dialog
import android.content.Context

class CustomDialog(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.dialog_course_image_name)
    }
}