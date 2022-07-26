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

const val ORDERS_MAX_LIMIT = 50
const val EVENTS_MAX_LIMIT = 200
const val ORDERS_ENDPOINT = "/v1/orders"
const val EVENTS_ENDPOINT = "/v1/events"
const val OFFERS_ENDPOINT = "/v1/offers"
const val CONTRACTS_ENDPOINT = "/v1/contracts/{contract}"
const val CONTRACTS_STATS_ENDPOINT = "/v1/contracts/{contract}/stats"
