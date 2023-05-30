package com.hansung.capstone.map

data class ResultGetAddress(
    var meta: AddressMeta, // 장소 메타데이터
    var documents: List<AddressResult> // 검색 결과
)

data class AddressMeta(
    var total_count: Int, // 검색어에 검색된 문서 수
)

data class AddressResult(
    var address: Address,
    var road_address: RoadAddress, // 장소 ID
)

data class Address(
    var address_name: String,
    var region_1depth_name: String,
    var region_2depth_name: String,
    var region_3depth_name: String,
)

data class RoadAddress(
    var address_name: String,
    var region_1depth_name: String,
    var region_2depth_name: String,
    var region_3depth_name: String,
)
