package com.example.elasticsearch.repository

import com.example.elasticsearch.dto.BoolSearchGoodsRequest
import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.dto.SearchGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.DeleteQuery
import org.springframework.stereotype.Repository

interface GoodsQueryRepository {
    fun deleteByQuery(request: DeleteGoodsRequest): Long
    fun searchByQuery(searchRequest: SearchGoodsRequest): List<GoodsDocument>
    fun boolSearchByQuery(request: BoolSearchGoodsRequest): List<GoodsDocument>
}


@Repository
class GoodsQueryRepositoryImpl(
    private val elasticsearchOperations: ElasticsearchOperations
): GoodsQueryRepository {


    override fun deleteByQuery(request: DeleteGoodsRequest): Long {
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

    override fun searchByQuery(request: SearchGoodsRequest): List<GoodsDocument> {
        var criteria = Criteria()

        request.name?.let { name ->
            criteria = criteria.and(Criteria("name").matches(name))
        }

        request.description?.let { description ->
            criteria = criteria.and(Criteria("description").matches(description))
        }

        request.adminMemo?.let { adminMemo ->
            criteria = criteria.and(Criteria("adminMemo").`is`(adminMemo))
        }

        val query = CriteriaQuery(criteria)
        return elasticsearchOperations.search(query, GoodsDocument::class.java)
            .map { it.content }
            .toList()
    }

    override fun boolSearchByQuery(request: BoolSearchGoodsRequest): List<GoodsDocument> {
        val criteriaList = mutableListOf<Criteria>()

        // must: 반드시 일치해야 하는 조건
        request.mustName?.let { name ->
            criteriaList.add(Criteria("name").matches(name))
        }

        request.mustDescription?.let { description ->
            criteriaList.add(Criteria("description").matches(description))
        }

        // must_not: 반드시 일치하지 않아야 하는 조건
        request.mustNotName?.let { name ->
            criteriaList.add(Criteria("name").not().matches(name))
        }

        request.mustNotAdminMemo?.let { memo ->
            criteriaList.add(Criteria("adminMemo").not().`is`(memo))
        }

        // filter: 반드시 일치하지만 스코어에 영향 없음 (범위 쿼리)
        if (request.filterMinPrice != null && request.filterMaxPrice != null) {
            criteriaList.add(Criteria("price").between(request.filterMinPrice, request.filterMaxPrice))
        } else if (request.filterMinPrice != null) {
            criteriaList.add(Criteria("price").greaterThanEqual(request.filterMinPrice))
        } else if (request.filterMaxPrice != null) {
            criteriaList.add(Criteria("price").lessThanEqual(request.filterMaxPrice))
        }

        // should: 하나라도 일치하면 더 높은 점수
        request.shouldDescription?.let { descriptions ->
            if (descriptions.isNotEmpty()) {
                val shouldCriteria = descriptions.map { desc ->
                    Criteria("description").matches(desc)
                }.reduce { acc, c -> acc.or(c) }
                criteriaList.add(shouldCriteria)
            }
        }

        // 조건이 없으면 전체 조회
        val finalCriteria = if (criteriaList.isEmpty()) {
            Criteria()
        } else {
            criteriaList.reduce { acc, c -> acc.and(c) }
        }

        val query = CriteriaQuery(finalCriteria)
        return elasticsearchOperations.search(query, GoodsDocument::class.java)
            .map { it.content }
            .toList()
    }
}
