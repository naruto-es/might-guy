package com.example.elasticsearch.controller

import com.example.elasticsearch.dto.CreateGoodsRequest
import com.example.elasticsearch.service.command.GoodsCommandService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/goods/async")
class GoodsAsyncController(
    private val goodsCommandService: GoodsCommandService
) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun createGoodsAsync(@RequestBody request: CreateGoodsRequest): Map<String, String> {
        goodsCommandService.createGoodsAsync(request)

        return mapOf("status" to "ACCEPTED", "message" to "Task submitted for processing")
    }
}
