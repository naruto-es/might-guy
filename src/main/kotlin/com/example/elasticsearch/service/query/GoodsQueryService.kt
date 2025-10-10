package com.example.elasticsearch.service.query

import com.example.elasticsearch.dto.BoolSearchGoodsRequest
import com.example.elasticsearch.dto.SearchGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.repository.GoodsRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class GoodsQueryService(
    private val goodsRepository: GoodsRepository,
) {

    fun getGoodsList(pageable: Pageable): List<GoodsDocument> {
        return goodsRepository.findAll(pageable).content
    }

    fun getGoodsListByQuery(request: SearchGoodsRequest): List<GoodsDocument> {
        return goodsRepository.searchByQuery(request)
    }

    fun boolSearch(request: BoolSearchGoodsRequest): List<GoodsDocument> {
        return goodsRepository.boolSearchByQuery(request)
    }
}
