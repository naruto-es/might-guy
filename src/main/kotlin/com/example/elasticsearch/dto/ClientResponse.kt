package com.example.elasticsearch.dto

data class ClientResponse<T>(
    val message: String? = null,
    val value: T? = null,
) {
    companion object Ably {
        @JvmStatic
        fun <T> success(value: T? = null) =
            ClientResponse("요청이 성공했습니다.", value)

        @JvmStatic
        fun fail(message: String? = "요청이 실패했습니다."): ClientResponse<Nothing> =
            ClientResponse(message)
    }
}