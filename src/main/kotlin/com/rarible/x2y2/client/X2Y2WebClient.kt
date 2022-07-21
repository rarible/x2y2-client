package com.rarible.x2y2.client

import com.rarible.x2y2.client.model.ApiListResponse
import com.rarible.x2y2.client.model.ApiSingleResponse
import com.rarible.x2y2.client.model.Contract
import com.rarible.x2y2.client.model.ContractStats
import com.rarible.x2y2.client.model.Event
import com.rarible.x2y2.client.model.Order
import java.math.BigInteger
import java.time.Instant
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody

@Component
class X2Y2WebClient(
    private val webClient: WebClient
) {

    /**
     * Fetch open orders/offers
     *
     * @param maker         Filter by the order maker's wallet address.
     * @param contract      Filter by contract address.
     * @param tokenId       Filter by the token ID of contract. Needs to be sent together with contract.
     * @param createdBefore Filter orders listed before this timestamp.
     * @param createdAfter  Filter orders listed after this timestamp.
     * @param sort          The property to sort the results by. (created_at, price) Default created_at
     * @param direction     The order to sort by. (asc, desc)
     * @param limit         Number of orders to return (capped at 50). Default 50.
     * @param cursor        A cursor to be supplied as a query param to retrieve the next page.
     * @param offers        Fetch offers. Default false
     */
    suspend fun orders(
        maker: String? = null,
        contract: String? = null,
        tokenId: BigInteger? = null,
        createdBefore: Instant? = null,
        createdAfter: Instant? = null,
        sort: String = "created_at",
        direction: String = "asc",
        limit: Int = ORDERS_MAX_LIMIT,
        cursor: String? = null,
        offers: Boolean = false
    ): ApiListResponse<Order> {
        val endpoint = if (offers) OFFERS_ENDPOINT else ORDERS_ENDPOINT
        return webClient.get().uri(endpoint) {builder ->
            maker?.let { builder.queryParam("maker", it) }

            if (contract != null && tokenId != null) {
                builder.queryParam("contract", contract)
                builder.queryParam("token_id", tokenId)
            }

            createdBefore?.let { builder.queryParam("created_before", createdBefore.epochSecond) }
            createdAfter?.let { builder.queryParam("created_after", createdAfter.epochSecond) }
            cursor?.let { builder.queryParam("cursor", cursor) }
            builder.queryParam("sort", sort).queryParam("direction", direction).queryParam("limit", limit)
            builder.build()
        }.retrieve().awaitBody()
    }

    /**
     * Fetch events
     *
     * @param type          Filter by the event type. Required.
     * @param contract      Filter by contract address.
     * @param tokenId       Filter by the token ID of contract. Needs to be sent together with contract.
     * @param fromAddress   Filter by event from address.
     * @param toAddress     Filter by event to address.
     * @param createdBefore Filter orders listed before this timestamp.
     * @param createdAfter  Filter orders listed after this timestamp.
     * @param limit         Number of events to return (capped at 200). Default 200.
     * @param cursor        A cursor to be supplied as a query param to retrieve the next page.
     */
    suspend fun events(
        type: String,
        contract: String? = null,
        tokenId: BigInteger? = null,
        fromAddress: String? = null,
        toAddress: String? = null,
        createdBefore: Instant? = null,
        createdAfter: Instant? = null,
        limit: Int = EVENTS_MAX_LIMIT,
        cursor: String? = null
    ): ApiListResponse<Event> {
        return webClient.get().uri(EVENTS_ENDPOINT) { builder ->

            builder.queryParam("type", type)
            if (contract != null && tokenId != null) {
               builder.queryParam("contract", contract)
                   .queryParam("token_id", tokenId)
            }

            fromAddress?.let { builder.queryParam("from_address", fromAddress) }
            toAddress?.let { builder.queryParam("to_address", toAddress) }
            createdBefore?.let { builder.queryParam("created_before", createdBefore.epochSecond) }
            createdAfter?.let { builder.queryParam("created_after", createdAfter.epochSecond) }
            cursor?.let { builder.queryParam("cursor", cursor) }
            builder.queryParam("limit", limit)
            builder.build()
        }.retrieve().awaitBody()
    }

    /**
     * Fetch contract
     *
     * @param contract Filter by contract address
     */
    suspend fun contract(contract: String): ApiSingleResponse<Contract> {
        return webClient.get().uri(CONTRACTS_ENDPOINT, mapOf("contract" to contract)).retrieve().awaitBody()
    }

    /**
     * Fetch contract stats
     */
    suspend fun contractStats(contract: String): ApiSingleResponse<ContractStats> {
        return webClient.get().uri(CONTRACTS_STATS_ENDPOINT, mapOf("contract" to contract)).retrieve().awaitBody()
    }

    companion object {
        private const val ORDERS_MAX_LIMIT = 50
        private const val EVENTS_MAX_LIMIT = 200
        private const val ORDERS_ENDPOINT = "/v1/orders"
        private const val EVENTS_ENDPOINT = "/v1/events"
        private const val OFFERS_ENDPOINT = "/v1/offers"
        private const val CONTRACTS_ENDPOINT = "/v1/contracts/{contract}"
        private const val CONTRACTS_STATS_ENDPOINT = "/v1/contracts/{contract}/stats"
    }
}
