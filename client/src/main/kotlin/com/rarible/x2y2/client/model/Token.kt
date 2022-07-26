package com.rarible.x2y2.client.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import scalether.domain.Address
import java.math.BigInteger

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Token(
    val tokenId: BigInteger?,
    val ercType: ErcType,
    val contract: Address
)