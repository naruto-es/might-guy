package com.example.elasticsearch.repository

import com.example.elasticsearch.dto.BoolSearchGoodsRequest
import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.dto.SearchGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.DeleteQuery
import org.springframework.data.mapping.toDotPath
import org.springframework.stereotype.Repository

interface GoodsQueryRepository {
    fun deleteByQuery(request: DeleteGoodsRequest): Long
    fun searchByQuery(searchRequest: SearchGoodsRequest): List<GoodsDocument>
    fun boolSearchByQuery(request: BoolSearchGoodsRequest): List<GoodsDocument>
}


@Repository
class GoodsQueryRepositoryImpl(
    private val elasticsearchOperations: ElasticsearchOperations,
) : GoodsQueryRepository {


    override fun deleteByQuery(request: DeleteGoodsRequest): Long {
        var criteria = Criteria()

        request.name?.let { name ->
            criteria = criteria.and(Criteria("name").matches(name))
        }

        if (request.minPrice != null && request.maxPrice != null) {
            // price 문자열 리터럴 들어가는거 지양하는게 좋아보여요.
            criteria = criteria.and(
                // between 함수나 gte 함수 보면 any로 타입이 지정되어있다보니, DSL만들어서 포팅해주면 편할것 같죠?
                // 아규먼트가 nullable한 between 함수로 하나 만들면 아래 null check도 없어져서 사용하기 더 편해질거에요.
                Criteria(GoodsDocument::price.toDotPath()).between(request.minPrice, request.maxPrice)
            )
        } else if (request.minPrice != null) {
            criteria = criteria.and(Criteria("price").greaterThanEqual(request.minPrice))
        } else if (request.maxPrice != null) {
            criteria = criteria.and(Criteria("price").lessThanEqual(request.maxPrice))
        }

        // 확장함수 하나 만들면 사용하는 시점에 간결해지고, 재사용성도 높아지겠네요.
        return elasticsearchOperations.delete(criteria.toDeleteQuery(), GoodsDocument::class.java).deleted
    }

    private fun Criteria.toDeleteQuery(): DeleteQuery {
        return DeleteQuery.builder(CriteriaQuery(this)).build()
    }

    override fun searchByQuery(searchRequest: SearchGoodsRequest): List<GoodsDocument> {
        var criteria = Criteria()

        searchRequest.name?.let { name ->
            criteria = criteria.and(Criteria("name").matches(name))
        }

        searchRequest.description?.let { description ->
            criteria = criteria.and(Criteria("description").matches(description))
        }

        searchRequest.adminMemo?.let { adminMemo ->
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
        // reduce 함수 사용하면 읽는 입장에서 직관적이지 않아서, 가독성이 더 좋은 확장함수로 바꾸는게 좋아보여요.
        request.shouldDescription?.let { descriptions ->
            val shouldCriteria = or {
                descriptions.map { desc ->
                    Criteria("description").matches(desc)
                }
            }

            criteriaList.add(shouldCriteria)
        }

        // 조건이 없으면 전체 조회
        val finalCriteria = if (criteriaList.isEmpty()) Criteria() else criteriaList.andAll()

        val query = CriteriaQuery(finalCriteria)
        return elasticsearchOperations.search(query, GoodsDocument::class.java)
            .map { it.content }
            .toList()
    }
}

private fun or(block: () -> List<Criteria>) = block().reduce { acc, c -> acc.or(c) }
private fun Iterable<Criteria>.andAll() = this.reduce { acc, c -> acc.and(c) }