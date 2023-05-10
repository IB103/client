package com.hansung.capstone.retrofit

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

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
data class ReqDoubleCheckID(
    @SerializedName("email")
    val email:String
)
data class ReqDoubleCheckNickName(
    @SerializedName("email")
    val email:String,
    @SerializedName("nickname")
    val nickname:String

)
data class ReqWriting(
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content:String
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
    val userId:Int,
    @SerializedName("category")
    val category:String,
    @SerializedName("title")
    val title:String,
    @SerializedName("content")
    val content:String
)
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

