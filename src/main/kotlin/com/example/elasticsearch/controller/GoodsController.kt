package com.example.elasticsearch.controller

import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.dto.DeleteGoodsRequest
import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.service.GoodsService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/goods")
class GoodsController(
    private val goodsService: GoodsService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createGoods(@RequestBody request: CreateGoodsRequest): GoodsDocument {
        return goodsService.createGoods(request)
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    fun deleteGoods(@RequestBody request: DeleteGoodsRequest): Map<String, Long> {
        val deletedCount = goodsService.deleteByQuery(request)
        return mapOf("deleted" to deletedCount)
    }
}
