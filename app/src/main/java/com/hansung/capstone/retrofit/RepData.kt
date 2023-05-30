package com.hansung.capstone.retrofit

import com.google.gson.annotations.SerializedName
import com.hansung.capstone.recommend.UserRecommend
import retrofit2.http.POST

data class RepConfirm(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: TokenInfo
)

data class RepLogOut(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)

data class RepSend(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("timestamp")
    val timestamp: String
)

data class RepRank(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<RankData>
)

data class RankData(
    @SerializedName("profileImageId")
    val profileImageId: Long,
    @SerializedName("userNickname")
    val userNickname: String,
    @SerializedName("totalDistance")
    val totalDistance: Float,
    @SerializedName("distanceRank")
    val distanceRank: Int
)

data class RepGetRecord(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<RidingData>
)

data class RidingData(
    @SerializedName("ridingTime")
    val ridingTime: Long,
    @SerializedName("ridingDistance")
    val ridingDistance: Float,
    @SerializedName("createdDate")
    var createdDate: String,
    @SerializedName("calorie")
    val calorie: Int
)
//    val calorie: Long,
//    val date: Long,
//    val distance: Long
//)

data class RepFindId(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: List<String>
)

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
    val data: UserResponse

)

data class UserResponse(
    @SerializedName("check")
    val check: Boolean,
    @SerializedName("email")
    val email: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("birthday")
    val birthday: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("userId")
    val userId: Long,
    @SerializedName("profileImageId")
    val profileImageId: Long,
    @SerializedName("tokenInfo")
    val tokenInfo: TokenInfo
)

data class RespondToken(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: TokenInfo
)

data class TokenInfo(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
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
    @SerializedName("message")
    val msg: String

)

data class RepDoubleCheckNickName(
    @SerializedName("code")
    val code: Int,
    @SerializedName("message")
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
    val messageval: String,
    @SerializedName("data")
    val data: String

)

data class RepModifyNick(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val messageval: String,
    @SerializedName("data")
    val data: String

)

data class RepPost(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val messageval: String,
    @SerializedName("data")
    val data: PostData
)

data class PostData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("authorId")
    val authorId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("authorProfileImageId")
    val authorProfileImageId: Int,
    @SerializedName("commentList")
    val commentList: List<RepData>,
    @SerializedName("imageId")
    val imageID: List<Int>
)

data class CommentList(
    @SerializedName("id")
    val id: Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userNickname")
    val userNickname: String,
    @SerializedName("userProfileImageId")
    val userProfileImageId: Int
)

data class RepComment(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: PostDataComment

)

data class PostDataComment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("authorId")
    val authorId: Int,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("authorProfileImageId")
    val authorProfileImageId: Int,
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
    val content: String,
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userNickname")
    val userNickname: String,
    @SerializedName("userProfileImageId")
    val userProfileImageId: Int,
    @SerializedName("reCommentList")
    val reCommentList: List<Recomment>,
    @SerializedName("commentVoterId")
    val commentVoterId: List<Int>
)

data class Recomment(
    @SerializedName("id")
    val id: Int,
    @SerializedName("content")
    val content: String,
    @SerializedName("createDate")
    val createDate: String,
    @SerializedName("modifiedDate")
    val modifiedDate: String,
    @SerializedName("userId")
    val userId: Int,
    @SerializedName("userNickname")
    val userNickname: String,
    @SerializedName("userProfileImageId")
    val userProfileImageId: Int,
    @SerializedName("reCommentVoterId")
    val commentVoterId: List<Int>
)

data class RepCoursePost(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
//    @SerializedName("data")
//    val data :String,
)


data class RepRidingData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val msg: Boolean,
    @SerializedName("message")
    val message: String,
)

data class RepCourseDetailData(
    @SerializedName("code")
    val code: Int,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: UserRecommend,
)
