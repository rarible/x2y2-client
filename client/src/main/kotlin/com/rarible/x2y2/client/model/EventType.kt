package com.rarible.x2y2.client.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class EventType(@get:JsonValue val value: String) {
    LIST("list"),
    SALE("sale"),
    CANCEL_LISTING("cancel_listing"),
    OFFER_LISTING("offer_listing");

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromValue(value: String): EventType {
            return when(value) {
                LIST.value -> LIST
                SALE.value -> SALE
                CANCEL_LISTING.value -> CANCEL_LISTING
                OFFER_LISTING.value -> OFFER_LISTING
                else -> throw IllegalArgumentException("Unsupported event type value: $value")
            }
        }
    }
}
