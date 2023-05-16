package com.hansung.capstone

import com.naver.maps.geometry.LatLng
import kotlin.math.round
import kotlin.math.roundToLong

object DataConverter {
    fun decode(encodedPath: String): List<LatLng> {
        val len = encodedPath.length
        val path: MutableList<LatLng> = ArrayList()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(
                LatLng(
                    round(lat * 1e-6 * 10000000) / 10000000,
                    round(lng * 1e-6 * 10000000) / 10000000
                )
            )
        }
        return path
    }

    fun encode(path: List<LatLng>): String {
        var lastLat: Long = 0
        var lastLng: Long = 0
        val result = StringBuffer()
        for (point in path) {
            val lat = (point.latitude * 1e6).roundToLong()
            val lng = (point.longitude * 1e6).roundToLong()
            val dLat = lat - lastLat
            val dLng = lng - lastLng
            encode(dLat, result)
            encode(dLng, result)
            lastLat = lat
            lastLng = lng
        }
        return result.toString()
    }

    private fun encode(l: Long, result: StringBuffer) {
        var v = l
        v = if (v < 0) (v shl 1).inv() else v shl 1
        while (v >= 0x20) {
            result.append(Character.toChars((0x20 or (v and 0x1f).toInt()) + 63))
            v = v shr 5
        }
        result.append(Character.toChars((v + 63).toInt()))
    }
}