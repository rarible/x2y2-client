package com.rarible.x2y2.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigInteger
import java.time.Instant
import scalether.domain.Address

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Event(
    val id: BigInteger,
    val tx: String?,
    val type: String,
    val fromAddress: Address,
    val toAddress: Address?,
    val token: Token,
    val order: Order,
    val createdAt: Instant
)
