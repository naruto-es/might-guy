package com.example.elasticsearch.service

import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.model.GoodsDocumentId
import com.example.elasticsearch.repository.GoodsRepository
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class GoodsService(
    private val goodsRepository: GoodsRepository
) {
    private val idGenerator = AtomicLong(1)

    fun createGoods(request: CreateGoodsRequest): GoodsDocument {
        val goods = GoodsDocument(
            id = GoodsDocumentId(idGenerator.getAndIncrement()),
            name = request.name,
            price = request.price,
            description = request.description
        )
        return goodsRepository.save(goods)
    }
}
