package com.hansung.capstone.retrofit

import com.google.gson.annotations.SerializedName
import retrofit2.http.POST
data class Weather(
    @SerializedName("main") val main: Main,
    @SerializedName("weather") val weather: List<WeatherDetail>,
    @SerializedName("name") val cityName: String
)
data class Main(
    @SerializedName("temp") val temperature: Double,
    @SerializedName("pressure") val pressure: Double,
    @SerializedName("humidity") val humidity: Double
)

data class WeatherDetail(
    @SerializedName("description") val description: String
)

data class RepLogin(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data:UserResponse

)
data class UserResponse(
    @SerializedName("check")
    val check:Boolean,
    @SerializedName("email")
    val email: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("birthday")
    val birthday: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("userId")
    val userId:Int,
    @SerializedName("profileImageId")
    val profileImageId: Int,
    @SerializedName("tokenInfo")
    val tokenInfo:TokenInfo
)
data class TokenInfo(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshListener: String
)
data class RepRegister(
    @SerializedName("id")
    val id: Long,
    @SerializedName("nickname")
    val nickname: String


)
data class RepDoubleCheckID(
    @SerializedName("code")
    val code: Int,
    @SerializedName("msg")
    val msg: String

)
data class RepDoubleCheckNickName(
    @SerializedName("code")
    val code: Int,
    @SerializedName("msg")
    val msg: String

)
data class RepWriting(
    @SerializedName("code")
    val code: Int,
    @SerializedName("msg")
    val msg: POST
)
data class RepModifyPW(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val messageval :String,
    @SerializedName("data")
    val data:String

)
data class RepModifyNick(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val messageval :String,
    @SerializedName("data")
    val data:String

)
data class RepPost(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val messageval :String,
    @SerializedName("data")
    val data:PostData
)data class PostData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content :String,
    @SerializedName("createDate")
    val createDate:String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("authorId")
    val authorId: Int,
    @SerializedName("nickname")
    val nickname :String,
    @SerializedName("authorProfileImageId")
    val authorProfileImageId:Int,
    @SerializedName("commentList")
    val commentList: List<RepData>,
    @SerializedName("imageId")
    val imageID: List<Int>
)
data class CommentList(
    @SerializedName("id")
    val id: Int,
    @SerializedName("content")
    val content :String,
    @SerializedName("createDate")
    val createDate:String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userNickname")
    val userNickname :String,
    @SerializedName("userProfileImageId")
    val userProfileImageId: Int
)
data class RepComment(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val message :String,
    @SerializedName("data")
    val data:PostDataComment

)data class PostDataComment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title :String,
    @SerializedName("content")
    val content :String,
    @SerializedName("createDate")
    val createDate:String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("authorId")
    val authorId: Int,
    @SerializedName("nickname")
    val nickname :String,
    @SerializedName("authorProfileImageId")
    val authorProfileImageId:Int,
    @SerializedName("commentList")
    val commentList: List<RepData>,
    @SerializedName("imageId")
    val imageId: List<Int>,
    @SerializedName("posterVoterId")
    val posterVoterId: List<Int>
)
data class RepData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("content")
    val content :String,
    @SerializedName("createDate")
    val createDate:String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userNickname")
    val userNickname :String,
    @SerializedName("userProfileImageId")
    val userProfileImageId:Int,
    @SerializedName("reCommentList")
    val reCommentList:List<Recomment>,
    @SerializedName("commentVoterId")
    val commentVoterId:List<Int>
)
data class Recomment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("content")
    val content :String,
    @SerializedName("createDate")
    val createDate:String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userNickname")
    val userNickname :String,
    @SerializedName("userProfileImageId")
    val userProfileImageId:Int,
    @SerializedName("reCommentVoterId")
    val commentVoterId:List<Int>
)
