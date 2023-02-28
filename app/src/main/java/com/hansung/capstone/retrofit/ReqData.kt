package com.hansung.capstone.retrofit

import com.google.gson.annotations.SerializedName

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
    val userId:Int,
    @SerializedName("title")
    val title:String,
    @SerializedName("content")
    val content:String)
