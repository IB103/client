package com.hansung.capstone

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

object Utility {
    fun saveSnapshot(context: Context, bitmap: Bitmap): String {
        val fileName = "snapshot_${System.currentTimeMillis()}.jpg" // 저장할 파일 이름

        val directory = context.filesDir

        val file = File(directory, fileName)

        val outputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()

        return file.absolutePath
    }
}