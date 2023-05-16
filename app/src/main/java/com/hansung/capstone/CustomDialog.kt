package com.hansung.capstone

import android.app.Dialog
import android.content.Context
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.hansung.capstone.map.WaypointsSearchActivity

class CustomDialog(context: Context) : Dialog(context) {

    init {
        setContentView(R.layout.dialog_course_image_name)

//        val searchBox = findViewById<EditText>(R.id.placeName)
//        val searchButton = findViewById<ImageButton>(R.id.placeSearch)
//        val enrollButton = findViewById<Button>(R.id.enrollButton)
//        val cancelButton = findViewById<Button>(R.id.cancelButton)

//        searchButton?.setOnClickListener {
//            val intent = Intent(context, WaypointsSearchActivity::class.java)
//            startActivityForResult(intent, REQUEST_CODE)
//            dialog.dismiss()
//        }


//        enrollButton?.setOnClickListener {
//            dismiss()
//        }
//
//
//        cancelButton?.setOnClickListener {
//            dismiss()
//        }
    }

}