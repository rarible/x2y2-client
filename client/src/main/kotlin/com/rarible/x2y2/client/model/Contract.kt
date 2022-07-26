package com.rarible.x2y2.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigInteger
import scalether.domain.Address

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Contract(
    val name: String,
    val royaltyFee: BigInteger,
    val nsfw: Boolean,
    val verified: Boolean,
    val ercType: Int,
    val slug: String,
    val contract: Address,
    val suspicious: Boolean,
    val symbol: String
)
