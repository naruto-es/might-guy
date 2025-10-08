package com.example.elasticsearch.controller

import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.dto.SearchGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.service.command.GoodsCommandService
import com.example.elasticsearch.service.query.GoodsQueryService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/goods")
class GoodsController(
    private val goodsCommandService: GoodsCommandService,
    private val goodsQueryService: GoodsQueryService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGoods(@RequestBody request: CreateGoodsRequest): GoodsDocument {
        return goodsCommandService.createGoods(request)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    fun deleteGoods(@RequestBody request: DeleteGoodsRequest): Map<String, Long> {
        val deletedCount = goodsCommandService.deleteByQuery(request)
        return mapOf("deleted" to deletedCount)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getGoods(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): List<GoodsDocument> {
        return goodsQueryService.getGoodsList(page, size)
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    fun getGoodsByQuery(
        @RequestParam(value = "name", required = false) name: String?,
        @RequestParam(value = "description", required = false) description: String?
    ): List<GoodsDocument> {
        val request = SearchGoodsRequest(name = name, description = description)
        return goodsQueryService.getGoodsListByQuery(request)
    }
}
