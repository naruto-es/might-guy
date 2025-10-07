package com.example.elasticsearch.dto

import java.math.BigDecimal

data class CreateGoodsRequest(
    val name: String,
    val price: BigDecimal,
    val description: String
)
