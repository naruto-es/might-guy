package com.example.elasticsearch.dto

data class DeleteGoodsRequest(
    val name: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
)
