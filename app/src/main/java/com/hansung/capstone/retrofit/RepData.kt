package com.hansung.capstone.retrofit

import com.google.gson.annotations.SerializedName

data class RepLogin(
    @SerializedName("code")
    val code: Int,
    @SerializedName("nickname")
    val nickname: String

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