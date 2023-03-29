package com.hansung.capstone.map

data class LocationImageDTO(
    var documents: List<Documents>
)

data class Documents(
    var image_url: String
)
