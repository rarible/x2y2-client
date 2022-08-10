package com.rarible.x2y2.client.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.daonomic.rpc.domain.Word
import scalether.domain.Address
import java.math.BigInteger
import java.time.Instant

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Order(
    val id: BigInteger,
    val maker: Address,
    val currency: Address,
    val taker: Address?,
    val price: BigInteger,
    val createdAt: Instant,
    val itemHash: Word,
    val type: OrderType,
    val isCollectionOffer: Boolean,
    val endAt: Instant,
    val isBundle: Boolean,
    val side: Int,
    val status: OrderStatus,
    val amount: BigInteger,
    val token: Token?,
    val updatedAt: Instant
)

@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy::class)
data class FetchOrderSignRequest(
    val caller: String,
    val op: BigInteger,
    val items: List<Item>
) {
    val amountToEth
        get() = 0
    val amountToWeth
        get() = 0

    data class Item(
        val orderId: BigInteger,
        val currency: Address,
        val price: BigInteger,
        val tokenId: BigInteger?
    )
}
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FetchOrderSignResponse(
    val orderId: String,
    val input: String
)


@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class GetCancelInputRequest(
    val caller: String,
    val op: BigInteger,
    val items: List<Item>,
    val sign: String,
    val signMessage: String
) {
    data class Item(
        val orderId: BigInteger
    )
}

data class GetCancelInputResponse(
    val input: String
)