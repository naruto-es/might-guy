package com.example.elasticsearch.config

import com.example.elasticsearch.config.property.ElasticSearchProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories

@Configuration
@EnableElasticsearchRepositories
class ElasticsearchConfig(
    private val elasticSearchProperty: ElasticSearchProperty,
) : ElasticsearchConfiguration() {
    @Bean
    override fun clientConfiguration(): ClientConfiguration =
        ClientConfiguration.builder()
            .connectedTo(elasticSearchProperty.uri)
            .build()
}