package com.rarible.x2y2.client.model

sealed interface ApiResponse {
    val success: Boolean
    val next: String?
}

data class ApiListResponse<out T>(
    override val success: Boolean,
    override val next: String?,
    val data: List<T>
): ApiResponse

data class ApiSingleResponse<out T>(
    override val success: Boolean,
    override val next: String?,
    val data: T
): ApiResponse
