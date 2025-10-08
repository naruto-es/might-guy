package com.example.elasticsearch.dto

data class SearchGoodsRequest(
    val name: String? = null,
    val description: String? = null,
    val adminMemo: String? = null
)
