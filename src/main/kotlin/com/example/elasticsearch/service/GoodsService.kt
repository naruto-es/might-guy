package com.example.elasticsearch.service

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient
import co.elastic.clients.elasticsearch.core.IndexRequest
import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.model.GoodsDocumentId
import com.example.elasticsearch.repository.GoodsRepository
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.DeleteQuery
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicLong

@Service
class GoodsService(
    private val goodsRepository: GoodsRepository,
    private val elasticsearchAsyncClient: ElasticsearchAsyncClient,
    private val elasticsearchOperations: ElasticsearchOperations
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

    fun deleteByQuery(request: DeleteGoodsRequest): Long {
        var criteria = Criteria()

        request.name?.let { name ->
            criteria = criteria.and(Criteria("name").matches(name))
        }

        if (request.minPrice != null && request.maxPrice != null) {
            criteria = criteria.and(Criteria("price").between(request.minPrice, request.maxPrice))
        } else if (request.minPrice != null) {
            criteria = criteria.and(Criteria("price").greaterThanEqual(request.minPrice))
        } else if (request.maxPrice != null) {
            criteria = criteria.and(Criteria("price").lessThanEqual(request.maxPrice))
        }

        val criteriaQuery = CriteriaQuery(criteria)
        val deleteQuery = DeleteQuery.builder(criteriaQuery).build()
        val byQuery = elasticsearchOperations.delete(deleteQuery, GoodsDocument::class.java)
        return byQuery.deleted
    }
}
