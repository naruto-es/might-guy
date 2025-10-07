package com.example.elasticsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [
    FlywayAutoConfiguration::class
])
@ConfigurationPropertiesScan
class ElasticsearchApplication

fun main(args: Array<String>) {
    runApplication<ElasticsearchApplication>(*args)
}
