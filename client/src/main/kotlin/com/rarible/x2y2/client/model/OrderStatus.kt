package com.rarible.x2y2.client.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OrderStatus(@get:JsonValue val value: String) {
    OPEN("open"),
    CANCELLED("cancelled"),
    DONE("done");

    companion object {

        @JvmStatic
        @JsonCreator
        fun fromValue(value: String): OrderStatus {
            return when(value) {
                OPEN.value -> OPEN
                CANCELLED.value -> CANCELLED
                DONE.value -> DONE
                else -> throw IllegalArgumentException("Unsupported order status value: $value")
            }
        }
    }
}
