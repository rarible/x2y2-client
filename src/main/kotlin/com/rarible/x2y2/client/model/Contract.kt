package com.rarible.x2y2.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.sun.org.apache.xpath.internal.operations.Bool
import java.math.BigInteger

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Contract(
    val name: String,
    val royaltyFee: BigInteger,
    val nsfw: Boolean,
    val verified: Boolean,
    val ercType: Int,
    val slug: String,
    val contract: String,
    val suspicious: Boolean,
    val symbol: String
)
