package com.example.elasticsearch.service

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient
import co.elastic.clients.elasticsearch.core.IndexRequest
import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.model.GoodsDocumentId
import com.example.elasticsearch.repository.GoodsRepository
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicLong

@Service
class GoodsService(
    private val goodsRepository: GoodsRepository,
    private val elasticsearchAsyncClient: ElasticsearchAsyncClient
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

    fun createGoodsAsync(request: CreateGoodsRequest): CompletableFuture<String> {
        val id = idGenerator.getAndIncrement()
        val goods = mapOf(
            "id" to id,
            "name" to request.name,
            "price" to request.price,
            "description" to request.description
        )

        val indexRequest = IndexRequest.of<Map<String, Any>> { builder ->
            builder
                .index("goods")
                .id(id.toString())
                .document(goods)
        }

        val future = CompletableFuture<String>()

        elasticsearchAsyncClient.index(indexRequest).whenComplete { response, error ->
            if (error != null) {
                future.completeExceptionally(error)
            } else {
                future.complete(response.id())
            }
        }

        return future
    }
}
