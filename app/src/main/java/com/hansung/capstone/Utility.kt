package com.hansung.capstone

import android.content.Context
import android.graphics.Bitmap
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

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

    fun zoomToSeeWholeTrack(path: List<LatLng>, naverMap: NaverMap) {
        val bounds = LatLngBounds.Builder()
        if (path.isNotEmpty()) {
            for (pos in path) {
                bounds.include(pos)
            }
            naverMap.moveCamera(
                CameraUpdate.fitBounds(bounds.build(), 200,300,200,200).animate(CameraAnimation.Easing)
            )
        }
    }



    fun moveToMarker(pos: LatLng, naverMap: NaverMap) {
        val cameraUpdate = CameraUpdate.scrollAndZoomTo(pos, 15.0)
            .animate(CameraAnimation.Fly,1000)
        naverMap.moveCamera(cameraUpdate)
    }

    fun convertMsList(ms: MutableList<Long>): MutableList<String> {
        val tmp:MutableList<String> = mutableListOf()
        for(i in 0 until ms.size){
            var originalMs = ms[i]
            val hours = TimeUnit.MILLISECONDS.toHours(originalMs)
            originalMs -= TimeUnit.HOURS.toMillis(hours)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(originalMs)
            originalMs -= TimeUnit.MINUTES.toMillis(minutes)
            tmp.add(i,"${if (minutes < 10) "0" else ""}$hours"+"H"+
                    "${if (minutes < 10) "0" else ""}$minutes"+"M")
        }
        return tmp
    }
    fun convertMs(ms: Long): String {
        var originalMs = ms
        val hours = TimeUnit.MILLISECONDS.toHours(originalMs)
        originalMs -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(originalMs)
        originalMs -= TimeUnit.MINUTES.toMillis(minutes)

        return "${if (minutes < 10) "0" else ""}$hours" +"H"+
                "${if (minutes < 10) "0" else ""}$minutes" +"M"

    }

}