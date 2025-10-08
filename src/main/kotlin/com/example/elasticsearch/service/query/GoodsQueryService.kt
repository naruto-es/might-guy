package com.example.elasticsearch.service.query

import com.example.elasticsearch.dto.SearchGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.repository.GoodsQueryRepository
import com.example.elasticsearch.repository.GoodsRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class GoodsQueryService(
    private val goodsRepository: GoodsRepository,
    private val goodsQueryRepository: GoodsQueryRepository
) {

    fun getGoodsList(page: Int, size: Int = 10): List<GoodsDocument> {
        val pageable = PageRequest.of(page, size)
        return goodsRepository.findAll(pageable).content
    }

    fun getGoodsListByQuery(request: SearchGoodsRequest): List<GoodsDocument> {
        return goodsQueryRepository.searchByQuery(request)
    }
}
