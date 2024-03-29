package com.hansung.capstone.map

data class ResultSearchDirections(
    var routes: List<Geometry>, // 장소 메타데이터
    var waypoints: List<Waypoints> // 검색 결과
)

data class Geometry(
    var geometry: String,
)

data class Waypoints(
    var distance: Double,
    var name: String,
)





