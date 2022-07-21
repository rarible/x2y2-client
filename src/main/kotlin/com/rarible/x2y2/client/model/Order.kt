package com.rarible.x2y2.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigInteger
import java.time.Instant

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Order(
    val id: Int,
    val maker: String,
    val currency: String,
    val taker: String?,
    val price: BigInteger,
    val createdAt: Instant,
    val itemHash: String,
    val type: String,
    val isCollectionOffer: Boolean,
    val endAt: Instant,
    val isBundle: Boolean,
    val side: Int,
    val status: String,
    val token: Token?,
    val updatedAt: Instant
)
