package com.hansung.capstone

import java.time.LocalDateTime

data class ResultGetAllPost (
    var code: Int = 0,
    var success: Boolean,
    var message: String,
    var data: List<Posts>
)

data class Posts(
    var id: Int,
    var title: String = "",
    var content: String = "",
    var createdDate: String = "",
    var modifiedDate: String = "",
    var commentList: List<Comments>
)

data class Comments(
    var content: String = ""
)