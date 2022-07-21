package com.rarible.x2y2.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigInteger

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Token(
    val tokenId: BigInteger,
    val contract: String
)
