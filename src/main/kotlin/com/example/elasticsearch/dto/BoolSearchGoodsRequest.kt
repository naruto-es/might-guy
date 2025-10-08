package com.example.elasticsearch.dto

data class BoolSearchGoodsRequest(
    // must: 반드시 일치해야 하는 조건 (AND)
    val mustName: String? = null,
    val mustDescription: String? = null,

    // must_not: 반드시 일치하지 않아야 하는 조건 (NOT)
    val mustNotName: String? = null,
    val mustNotAdminMemo: String? = null,

    // filter: 반드시 일치해야 하지만 스코어링에 영향 없음
    val filterMinPrice: Double? = null,
    val filterMaxPrice: Double? = null,

    // should: 일치하면 더 높은 점수 (OR, 선택적)
    val shouldDescription: List<String>? = null,
    val minimumShouldMatch: Int? = 1  // should 조건 중 최소 몇 개가 일치해야 하는지
)
