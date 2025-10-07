package com.example.elasticsearch.config

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient
import co.elastic.clients.json.jackson.JacksonJsonpMapper
import co.elastic.clients.transport.rest_client.RestClientTransport
import com.example.elasticsearch.config.property.ElasticSearchProperty
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
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

    @Bean
    fun elasticsearchAsyncClient(): ElasticsearchAsyncClient {
        val restClient = RestClient.builder(
            HttpHost.create("http://${elasticSearchProperty.uri}")
        ).build()

        val transport = RestClientTransport(
            restClient,
            JacksonJsonpMapper()
        )

        return ElasticsearchAsyncClient(transport)
    }
}