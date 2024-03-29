package com.hansung.capstone.post

data class ResultGetPostDetail (
    var code: Int = 0,
    var success: Boolean,
    var message: String,
    var data: Post,
)

data class Post(
    var id: Int,
    var title: String,
    var content: String,
    var createdDate: String,
    var modifiedDate: String,
    var authorId: Long,//Int = 0,
    var courseId: Long,
    var nickname: String,
    var authorProfileImageId: Long,
    var commentList: List<Comments>,
    var imageId: List<Int>,
    var postVoterId: Set<Long>,
    var postScraperId:Set<Long>,
    // 좋아요 버튼 실험용
    var heartButtonCheck: Boolean,
    var starButtonCheck: Boolean,
)

data class Comments(
    var id : Long,
    var content: String,
    var createdDate: String,
    var modifiedDate: String,
    var userId : Long,
    var userNickname: String,
    var userProfileImageId: Long,
    var reCommentList: List<ReComments>,
    var commentVoterId: Set<Long>
)

data class ReComments(
    var id : Long,
    var content: String,
    var createdDate: String,
    var modifiedDate: String,
    var userId : Long,
    var userNickname: String,
    var userProfileImageId: Long,
    var reCommentVoterId: Set<Long>
)


