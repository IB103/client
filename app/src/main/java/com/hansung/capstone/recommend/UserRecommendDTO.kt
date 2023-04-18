package com.hansung.capstone.recommend

import com.hansung.capstone.post.Post

data class UserRecommendDTO(
    var code: Int = 0,
    var success: Boolean,
    var message: String,
    val totalPage: Int,
    var data: List<UserRecommend>,
)

data class UserRecommend(
    var coordinates: String, // 경로 좌표
    var region: String,
    var originToDestination: String,
    var postId: Long,
    var numOfFavorite: Int
)