package com.rarible.x2y2.client

import com.rarible.x2y2.client.model.ApiListResponse
import com.rarible.x2y2.client.model.ApiSingleResponse
import com.rarible.x2y2.client.model.CONTRACTS_ENDPOINT
import com.rarible.x2y2.client.model.CONTRACTS_STATS_ENDPOINT
import com.rarible.x2y2.client.model.Contract
import com.rarible.x2y2.client.model.ContractStats
import com.rarible.x2y2.client.model.EVENTS_ENDPOINT
import com.rarible.x2y2.client.model.EVENTS_MAX_LIMIT
import com.rarible.x2y2.client.model.Event
import com.rarible.x2y2.client.model.EventType
import com.rarible.x2y2.client.model.OFFERS_ENDPOINT
import com.rarible.x2y2.client.model.ORDERS_ENDPOINT
import com.rarible.x2y2.client.model.ORDERS_MAX_LIMIT
import com.rarible.x2y2.client.model.Order
import com.rarible.x2y2.client.model.OrdersSort
import com.rarible.x2y2.client.model.SortDirection
import java.math.BigInteger
import java.net.URI
import java.time.Instant
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.util.UriBuilder
import scalether.domain.Address

@Component
class X2Y2WebClient(
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    private val webClient: WebClient
) {

    /**
     * Fetch open orders
     *
     * @param maker         Filter by the order maker's wallet address.
     * @param contract      Filter by contract address.
     * @param tokenId       Filter by the token ID of [contract]. Needs to be sent together with [contract].
     * @param createdBefore Filter orders listed before this timestamp.
     * @param createdAfter  Filter orders listed after this timestamp.
     * @param sort          The property to sort the results by. Default [OrdersSort.CREATED_AT]
     * @param direction     The order to sort by. Default [SortDirection.ASC]
     * @param limit         Number of orders to return (capped at 50).  Default [ORDERS_MAX_LIMIT]
     * @param cursor        A cursor to be supplied as a query param to retrieve the next page.
     *
     * @throws IllegalArgumentException when [contract] and [tokenId] defined or not separately
     */
    suspend fun orders(
        maker: Address? = null,
        contract: Address? = null,
        tokenId: BigInteger? = null,
        createdBefore: Instant? = null,
        createdAfter: Instant? = null,
        sort: OrdersSort = OrdersSort.CREATED_AT,
        direction: SortDirection = SortDirection.ASC,
        limit: Int = ORDERS_MAX_LIMIT,
        cursor: String? = null,
    ): ApiListResponse<Order> {

        if ((tokenId != null && contract == null) || (tokenId == null && contract != null)) {
            throw IllegalArgumentException("Contract and token ID must be both defined or both undefined!")
        }

        return webClient.get().uri(ORDERS_ENDPOINT) { builder ->
            makeOrdersUrl(
                maker,
                builder,
                contract,
                tokenId,
                createdBefore,
                createdAfter,
                cursor,
                sort,
                direction,
                limit
            )
        }.retrieve().awaitBody()
    }

    /**
     * Fetch open offers endpoint
     *
     * @param maker             Filter by the offer maker's wallet address
     * @param contract          Filter by contract address. IF [tokenId] Is not specified, only collection offers will be returned.
     * @param tokenId           Filter by the token ID of [contract]. Needs to be sent together with [contract]. Collection offers will not be returned.
     * @param createdBefore     Filter offers created before this timestamp.
     * @param createdAfter      Filter offers created after this timestamp.
     * @param sort              The property to sort the results by. Default [OrdersSort.CREATED_AT]
     * @param direction         The order to sort by. Default [SortDirection.ASC]
     * @param limit             Number of orders to return (capped at 50). Default [ORDERS_MAX_LIMIT]
     * @param cursor            A cursor to be supplied as a query param to retrieve the next page
     *
     * @throws IllegalArgumentException when [maker] or [contract] is not defined or when [tokenId] is defined but [contract] is undefined
     */
    suspend fun offers(
        maker: Address? = null,
        contract: Address? = null,
        tokenId: BigInteger? = null,
        createdBefore: Instant? = null,
        createdAfter: Instant? = null,
        sort: OrdersSort = OrdersSort.CREATED_AT,
        direction: SortDirection = SortDirection.ASC,
        limit: Int = ORDERS_MAX_LIMIT,
        cursor: String? = null,
    ): ApiListResponse<Order> {
        if (maker == null && contract == null) {
            throw IllegalArgumentException("Maker or contract must be defined!")
        }

        if (tokenId != null && contract == null) {
            throw IllegalArgumentException("Contract must be defined if token ID is defined!")
        }

        return webClient.get().uri(OFFERS_ENDPOINT) { builder ->
            makeOrdersUrl(
                maker,
                builder,
                contract,
                tokenId,
                createdBefore,
                createdAfter,
                cursor,
                sort,
                direction,
                limit
            )
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
        type: EventType,
        contract: String? = null,
        tokenId: BigInteger? = null,
        fromAddress: Address? = null,
        toAddress: Address? = null,
        createdBefore: Instant? = null,
        createdAfter: Instant? = null,
        limit: Int = EVENTS_MAX_LIMIT,
        cursor: String? = null
    ): ApiListResponse<Event> {
        return webClient.get().uri(EVENTS_ENDPOINT) { builder ->

            builder.queryParam("type", type.value)
            if (contract != null && tokenId != null) {
                builder.queryParam("contract", contract)
                    .queryParam("token_id", tokenId)
            }

            fromAddress?.let { builder.queryParam("from_address", fromAddress.prefixed()) }
            toAddress?.let { builder.queryParam("to_address", toAddress.prefixed()) }
            createdBefore?.let { builder.queryParam("created_before", createdBefore.epochSecond) }
            createdAfter?.let { builder.queryParam("created_after", createdAfter.epochSecond) }
            cursor?.let { builder.queryParam("cursor", cursor) }
            builder.queryParam("limit", minOf(limit, EVENTS_MAX_LIMIT))
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

    private fun makeOrdersUrl(
        maker: Address?,
        builder: UriBuilder,
        contract: Address?,
        tokenId: BigInteger?,
        createdBefore: Instant?,
        createdAfter: Instant?,
        cursor: String?,
        sort: OrdersSort,
        direction: SortDirection,
        limit: Int
    ): URI {
        maker?.let { builder.queryParam("maker", it) }
        contract?.let { builder.queryParam("contract", contract.prefixed()) }
        tokenId?.let { builder.queryParam("token_id", tokenId) }
        createdBefore?.let { builder.queryParam("created_before", createdBefore.epochSecond) }
        createdAfter?.let { builder.queryParam("created_after", createdAfter.epochSecond) }
        cursor?.let { builder.queryParam("cursor", cursor) }

        builder
            .queryParam("sort", sort.name.lowercase())
            .queryParam("direction", direction.name.lowercase())
            .queryParam("limit", minOf(limit, ORDERS_MAX_LIMIT))

        return builder.build()
    }


}
