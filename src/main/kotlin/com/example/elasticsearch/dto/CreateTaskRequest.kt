package com.example.elasticsearch.dto

data class CreateTaskRequest(
    val title: String,
    val description: String,
    val status: String = "TODO"
)
