package com.rarible.x2y2.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigInteger
import java.time.Instant

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Event(
    val id: BigInteger,
    val tx: String?,
    val type: String,
    val fromAddress: String,
    val toAddress: String?,
    val token: Token,
    val order: Order,
    val createdAt: Instant
)
