package com.hansung.capstone

import android.location.Location
import java.util.concurrent.TimeUnit

object RidingUtility {
    fun convertMs(ms: Long): String {
        var originalMs = ms
        val hours = TimeUnit.MILLISECONDS.toHours(originalMs)
        originalMs -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(originalMs)
        originalMs -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(originalMs)

        return "${if (minutes < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

    fun calculateDistance(line: line): Float {
        var distance = 0f
        if (line.size > 2) {
            for (c in 0..line.size - 2) {
                val coordinate1 = line[c]
                val coordinate2 = line[c + 1]

                val result = FloatArray(1)
                Location.distanceBetween(
                    coordinate1.latitude,
                    coordinate1.longitude,
                    coordinate2.latitude,
                    coordinate2.longitude,
                    result
                )
                distance += result[0]
            }
        }
        return distance/1000 // m -> km 반환
    }

    fun calculateSpeed(ms: Long, distance: Float): Float {
        // 속도(km/h) = (거리 / 1000) / (초 / 3600)
        return if(ms>0)
            distance / (ms.toFloat() / 1000 / 3600)
        else
            0f
    }

    fun calculateKcal(ms: Long): Float {
        // 칼로리 소모량 (kcal) = 시간 * 몸무게 * 5.0(METs) * 1.05
        return (ms.toFloat() / 1000 / 3600) * 65 * 4.0f * 1.05f
    }
}