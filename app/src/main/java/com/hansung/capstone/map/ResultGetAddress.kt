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
//    var address: Address, // 장소명, 업체명
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
//    var road_name: String,
//    var underground_yn: String,
//    var main_building_no: String,
//    var sub_building_no: String,
//    var building_name: String,
//    var zone_no: String,
)

//data class Address(
//    var address_name: String,
//    var region_1depth_name: String,
//    var region_2depth_name: String,
//    var region_3depth_name: String,
//    var mountain_yn: String,
//    var main_address_no: String,
//    var sub_address_no: String,
//)
