package com.example.elasticsearch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication(exclude = [
    FlywayAutoConfiguration::class
])
@ConfigurationPropertiesScan
@EnableAsync
class ElasticsearchApplication

fun main(args: Array<String>) {
    runApplication<ElasticsearchApplication>(*args)
}
