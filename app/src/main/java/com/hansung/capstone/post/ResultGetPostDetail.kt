package com.hansung.capstone.post

data class ResultGetPostDetail (
    var code: Int = 0,
    var success: Boolean,
    var message: String,
    var data: Post,
)

data class Post(
    var id: Int,
    var title: String = "",
    var content: String = "",
    var createdDate: String = "",
    var modifiedDate: String = "",
    var authorId: Int = 0,
    var commentList: List<Comments>,
    var imageId: List<Int>,
)

data class Comments(
    var content: String = ""
)