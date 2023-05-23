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
}