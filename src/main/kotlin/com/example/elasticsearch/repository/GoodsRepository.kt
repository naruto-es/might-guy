package com.example.elasticsearch.repository

import com.example.elasticsearch.model.GoodsDocument
import com.example.elasticsearch.model.GoodsDocumentId
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface GoodsRepository: ElasticsearchRepository<GoodsDocument, GoodsDocumentId> {
    fun findAllByName(name: String): List<GoodsDocument>
    fun findAllByNameContainingIgnoreCase(name: String): List<GoodsDocument>
}