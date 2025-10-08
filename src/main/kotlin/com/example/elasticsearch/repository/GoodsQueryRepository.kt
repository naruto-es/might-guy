package com.example.elasticsearch.repository

import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.dto.SearchGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.DeleteQuery
import org.springframework.stereotype.Repository

@Repository
class GoodsQueryRepository(
    private val elasticsearchOperations: ElasticsearchOperations
) {

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

    fun searchByQuery(request: SearchGoodsRequest): List<GoodsDocument> {
        var criteria = Criteria()

        request.name?.let { name ->
            criteria = criteria.and(Criteria("name").matches(name))
        }

        request.description?.let { description ->
            criteria = criteria.and(Criteria("description").matches(description))
        }

        val query = CriteriaQuery(criteria)
        return elasticsearchOperations.search(query, GoodsDocument::class.java)
            .map { it.content }
            .toList()
    }
}
