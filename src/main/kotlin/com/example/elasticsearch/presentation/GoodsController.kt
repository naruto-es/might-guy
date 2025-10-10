package com.example.elasticsearch.presentation

import com.example.elasticsearch.dto.BoolSearchGoodsRequest
import com.example.elasticsearch.dto.ClientResponse
import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.dto.SearchGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.service.command.GoodsCommandService
import com.example.elasticsearch.service.query.GoodsQueryService
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
// @RequestMapping 안쓰는게 파일 찾아 다닐 때 더 좋아요. api 전체 경로로 찾을 수 있으니까요.
@RequestMapping("/api/goods")
class GoodsController(
    private val goodsCommandService: GoodsCommandService,
    private val goodsQueryService: GoodsQueryService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGoods(@RequestBody request: CreateGoodsRequest): GoodsDocument {
        return goodsCommandService.createGoods(request)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    fun deleteGoods(@RequestBody request: DeleteGoodsRequest): ClientResponse<Map<String, Long>> {
        val deletedCount = goodsCommandService.deleteByQuery(request)
        // 응답은 ClientResponse를 만들어서 일정한 양식으로 보내는게 좋아요. 프론트엔드에서 응답을 처리할 때 편하거든요.
        return ClientResponse.success(mapOf("deleted" to deletedCount))
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getGoods(
        // page, size 쿼리 파라미터로 받는 것보다 Pageable로 받는게 더 편해요. 참고로 PageableDefault로 기본값도 줄 수 있어요.
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
    ): List<GoodsDocument> {
        return goodsQueryService.getGoodsList(pageable)
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    // query parameter로 받는거보다 @RequestModelAttribute로 받는게 더편해요. 그리고 dto 안에 있는 필드에도 request parameter로 받을 수 있어요.
    // 그리고 위 getGoods api랑 합치고, /search는 삭제하는게 좋아보여요.
    fun getGoodsByQuery(@ModelAttribute request: SearchGoodsRequest): List<GoodsDocument> {
        return goodsQueryService.getGoodsListByQuery(request)
    }

    @PostMapping("/search/bool")
    @ResponseStatus(HttpStatus.OK)
    fun boolSearch(@RequestBody request: BoolSearchGoodsRequest): List<GoodsDocument> {
        return goodsQueryService.boolSearch(request)
    }
}
