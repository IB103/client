package com.hansung.capstone.retrofit

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName


data class ReqModifyPost(
    var postId:Long,
    var title:String,
    var userId:Long,
    var content:String,
    var imageId: ArrayList<Long?>

)
data class ReqModifyProfileImage(
    var userId:Long,
    var profileImageId:Long

    )

data class ReqModifyComment(
    @SerializedName("commentId")
    val commentId: Long,

    @SerializedName("userId")
    val  userId: Long,
    @SerializedName("content")
    val  content: String
)
data class ReqModifyReComment(
    @SerializedName("reCommentId")
    val reCommentId: Long,
    @SerializedName("userId")
    val  userId: Long,
    @SerializedName("content")
    val  content: String
)
data class ReqLogin(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)
data class ReqRegister(
@SerializedName("email")
val email: String,
@SerializedName("password")
val password:String,
@SerializedName("nickname")
val nickname:String,
@SerializedName("username")
val username:String,
@SerializedName("birthday")
val birthday:String

)


data class ReqModifyPW(
    @SerializedName("email")
    val email:String,
    @SerializedName("password")
    val password:String
)
data class ReqModifyNick(
    @SerializedName("email")
    val email:String,
    @SerializedName("nickname")
    val nickname:String
)

data class ReqPost(
    @SerializedName("userId")
    val userId:Long,
    @SerializedName("title")
    val title:String,
    @SerializedName("category")
    val category:String,
    @SerializedName("content")
    val content:String
    )

data class ReqCoursePost(
    @SerializedName("coordinates")
    var coordinates: String,
    @SerializedName("region")
    var region: String,
    @SerializedName("originToDestination")
    var originToDestination: String,
    @SerializedName("userId")
    val userId:Long,
    @SerializedName("category")
    val category:String,
    @SerializedName("title")
    val title:String,
    @SerializedName("content")
    val content:String,
    @SerializedName("imageInfoList")
    val imageInfoList: List<ImageInfo>
)
data class ReqRidingData(
    @SerializedName("ridingTime")
    var ridingTime: Long,
    @SerializedName("ridingDistance")
    var ridingDistance: Float,
    @SerializedName("calorie")
    var calorie: Int,
    @SerializedName("userId")
    var userId:Long,
)
//data class ImageInfo(
//    val coordinate:String,
//    val placeName:String,
//    val placeLink:String,
//)
data class ImageInfo(
    val coordinate: String,
    val placeName: String,
    val placeLink: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(coordinate)
        parcel.writeString(placeName)
        parcel.writeString(placeLink)
    }

    companion object CREATOR : Parcelable.Creator<ImageInfo> {
        override fun createFromParcel(parcel: Parcel): ImageInfo {
            return ImageInfo(parcel)
        }

        override fun newArray(size: Int): Array<ImageInfo?> {
            return arrayOfNulls(size)
        }
    }
}

data class ReqComment(
    @SerializedName("postId")
    val postId:Long,
    @SerializedName("userId")
    val userId:Long,
    @SerializedName("content")
    val content:String

)
data class ReqReComment(
    @SerializedName("postId")
    val postId:Long,
    @SerializedName("commentId")
    val commentId:Long,
    @SerializedName("userId")
    val userId:Long,
    @SerializedName("content")
    val content:String
)

