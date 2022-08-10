package com.rarible.x2y2.client.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class ErcType(@get:JsonValue val value: String) {
    ERC721("erc721"),
    ERC1155("erc1155")
    ;

    companion object {
        @JsonCreator
        @JvmStatic
        fun fromValue(value: String): ErcType {
            return when(value) {
                ERC721.value -> ERC721
                ERC1155.value -> ERC1155
                else -> throw IllegalArgumentException("Unsupported erc type value: $value")
            }
        }
    }
}