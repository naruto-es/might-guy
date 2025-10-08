package com.example.elasticsearch.model

import co.elastic.clients.elasticsearch._types.analysis.Normalizer
import com.example.elasticsearch.config.constant.Analyzer
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.math.BigDecimal

@JvmInline
value class GoodsDocumentId(val id: Long) {}

@Document(indexName = "goods")
data class GoodsDocument(
    @Id
    @field:Field(type = FieldType.Long)
    val id: GoodsDocumentId,

    @field:Field(type = FieldType.Keyword)
    val name: String,

    @field:Field(type = FieldType.Double)
    val price: BigDecimal,

    @field:Field(type = FieldType.Text, analyzer = Analyzer.KOREAN_ANALYZER, searchAnalyzer = Analyzer.KOREAN_ANALYZER)
    val description: String,

    @field:Field(type = FieldType.Keyword, normalizer = "lowercase_normalizer")
    val adminMemo: String,
)
