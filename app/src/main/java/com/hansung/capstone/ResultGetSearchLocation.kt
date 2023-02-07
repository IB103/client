package com.hansung.capstone

data class ResultGetSearchLocation(
    var lastBuildDate: String = "",
    var total: Int = 0,
    var start: Int = 0,
    var display: Int = 0,
    var items: List<Items>
)

data class Items(
    var title: String = "",
    var link: String = "",
    var category: String = "",
    var description: String ="",
    var telephone: String ="",
    var address: String = "",
    var roadAddress: String = "",
    var mapx: Int? = null,
    var mapy: Int? = null,
)


