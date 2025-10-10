package com.example.elasticsearch.service.command

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient
import co.elastic.clients.elasticsearch.core.IndexRequest
import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.model.GoodsDocumentId
import com.example.elasticsearch.repository.GoodsRepository
import org.springframework.data.mapping.toDotPath
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicLong

@Service
class GoodsCommandService(
    private val goodsRepository: GoodsRepository,
    private val elasticsearchAsyncClient: ElasticsearchAsyncClient,
) {
    // idGenerator 이거는 scale out 할 때 동일하게 동시성이 보장되지 않는데, 다른 방법으로 고쳐보는게 좋을 것 같아요.
    private val idGenerator = AtomicLong(1)

    fun createGoods(request: CreateGoodsRequest): GoodsDocument {
        val goods = GoodsDocument(
            id = GoodsDocumentId(idGenerator.getAndIncrement()),
            name = request.name,
            price = request.price,
            description = request.description,
            adminMemo = request.adminMemo
        )
        return goodsRepository.save(goods)
    }

    fun createGoodsAsync(request: CreateGoodsRequest): CompletableFuture<String> {
        val id = idGenerator.getAndIncrement()
        // service layer에는 문자열 리터럴 최대한 지양하는게 좋을 것 같아요.
        val goods = mapOf(
            GoodsDocument::id.toDotPath() to id,
            GoodsDocument::name.toDotPath() to request.name,
            GoodsDocument::price.toDotPath() to request.price,
            GoodsDocument::description.toDotPath() to request.description,
            GoodsDocument::adminMemo.toDotPath() to request.adminMemo
        )

        // 불필요한 제네릭 삭제하는게 좋아보여요
        val indexRequest = IndexRequest.of { builder ->
            builder
                // 인덱스도 상수로 관리하는게 좋아보여요.
                .index("goods")
                .id(id.toString())
                .document(goods)
        }

        // 선언부를 기준으로 apply 함수 사용하면 인스턴스가 없어져서 더 깔끔해져요.(코루틴으로 하면 CompletableFuture를 안써서 더 좋겠지만 넘어갈게요)
        return CompletableFuture<String>().apply {
            elasticsearchAsyncClient.index(indexRequest).whenComplete { response, error ->
                if (error != null) completeExceptionally(error)
                else complete(response.id())
            }
        }
    }

    fun deleteByQuery(request: DeleteGoodsRequest): Long {
        return goodsRepository.deleteByQuery(request)
    }
}
