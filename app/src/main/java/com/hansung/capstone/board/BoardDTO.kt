package com.hansung.capstone.board

import okhttp3.MultipartBody


data class ResultRespond(
    var code: Int,
    var success: Boolean,
    var message: String,
)
data class RePModifyProfileImage(
    var code: Int,
    var success: Boolean,
    var message: String,
    var data: ModifiedProfile
)
data class ModifiedProfile(
    var id: Long,
    var email: String,
    var username: String,
    var birthday: String,
    var nickname: String,
    var profileImageId:Long
)
data class ModifyPost(
    var code: Int,
    var success: Boolean,
    var message: String,
    var totalPage: Int,
    var data: Posts
)
data class ResultGet(
    var code: Int,
    var success: Boolean,
    var message: String
)
data class ResultGetPosts(
    var code: Int,
    var success: Boolean,
    var message: String,
    var totalPage: Int,
    var data: List<Posts>
)

data class Posts(
    var id: Long,
    var title: String,
    var content: String,
    var createdDate: String,
    var modifiedDate: String,
    var authorId: Long,
    var nickname: String,
    var authorProfileImageId: Long,
    var commentList: List<Comments>,
    var imageId: List<Long>,
    var postVoterId: Set<Long>,
    var postScraperId: Set<Long>,
    // 게시판에 들어갈 item type 설정
    var postType : Int,
    var commentChanged:Boolean,
    var heartChanged:Boolean,
    var commentCount:Int
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
data class ResDelete(
    var code: Int,
    var success:Boolean,
    var message: String
)
data class ResDeleteReComment(
    var code: Int,
    var success:Boolean,
    var message: String,
    var data:Boolean
)