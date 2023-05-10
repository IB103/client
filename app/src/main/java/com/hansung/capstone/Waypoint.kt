package com.hansung.capstone

import android.os.Parcel
import android.os.Parcelable

data class Waypoint(
    var place_name: String? = null,
    var place_lat: Double? = null,
    var place_lng: Double? = null,
    var place_url: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readString()
    )

    // Parcelable 인터페이스에서 필요한 메서드입니다.
    override fun describeContents(): Int {
        return 0
    }

    // Parcelable 인터페이스를 구현하기 위한 메서드입니다.
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(place_name)
        parcel.writeValue(place_lat)
        parcel.writeValue(place_lng)
        parcel.writeString(place_url)
    }

    // CREATOR 상수를 정의하여 Parcelable.Creator 인터페이스를 구현합니다.
    companion object CREATOR : Parcelable.Creator<Waypoint> {
        // Parcel에서 Waypoint 객체를 생성합니다.
        override fun createFromParcel(parcel: Parcel): Waypoint {
            return Waypoint(parcel)
        }

        // Waypoint 객체의 배열을 생성합니다.
        override fun newArray(size: Int): Array<Waypoint?> {
            return arrayOfNulls(size)
        }
    }
}