package com.rarible.x2y2.client.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OrderType(@get:JsonValue val value: String) {
    SELL("sell"),
    BUY("buy");

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromValue(value: String): OrderType {
            return when(value) {
                SELL.value -> SELL
                BUY.value -> BUY
                else -> throw IllegalArgumentException("Unsupported order type value: $value")
            }
        }
    }
}
