package com.example.elasticsearch.dto

import org.springframework.web.bind.annotation.RequestParam

data class SearchGoodsRequest(
    @RequestParam(required = false)
    val name: String? = null,

    @RequestParam(required = false)
    val description: String? = null,

    // RequestParam을 붙히면 클라이언트에서 받는 파라미터의 이름을 바꿀 수 있어요.
    @RequestParam(value = "memo", required = false)
    val adminMemo: String? = null,
)
