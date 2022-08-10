package com.rarible.x2y2.starter

import com.rarible.x2y2.client.X2Y2ApiClient
import com.rarible.x2y2.client.model.Event
import com.rarible.x2y2.client.model.EventType
import com.rarible.x2y2.client.model.Order
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import scalether.domain.Address
import java.math.BigInteger

/**
 * To run this test put real x2y2 api key to application-local.yml
 */
@SpringBootTest
@SpringBootConfiguration
@EnableAutoConfiguration
@ActiveProfiles("local")
class X2Y2ClientIntegrationTest {

    @Autowired
    private lateinit var client: X2Y2ApiClient

    @Test
    fun `test default client initialized`() {
        assertThat(client).isNotNull
    }

    @Test
    @Disabled
    internal fun `should read order with paging`() {
        val expectedCount = 200
        runBlocking {
            val orders = mutableListOf<Order>()
            var cursor: String? = null
            for (i in 1..4) {
                val result = client.orders(cursor = cursor)
                orders.addAll(result.data)
                cursor = result.next
            }

            assertThat(orders.size).isEqualTo(expectedCount)
        }
    }

    @Test
    @Disabled
    internal fun `should read events with paging`() {
        runBlocking {
            val expectedCount = 800
            val events = mutableListOf<Event>()
            var cursor: String? = null
            for (i in 1..4) {
                val result = client.events(type = EventType.LIST, cursor = cursor)
                events.addAll(result.data)
                cursor = result.next
            }

            assertThat(events.size).isEqualTo(expectedCount)
        }
    }

    @Test
    @Disabled
    internal fun `should sign order`() = runBlocking<Unit> {
        val orders = client.orders(
            contract = Address.apply("0x259bF444f0bFE8Af20b6097cf8D32A85526B03a4"),
            tokenId = BigInteger.valueOf(999)
        )
        assertThat(orders.data.isNotEmpty()).isTrue

        val order = orders.data.first()
        val response = client.fetchOrderSign(
            "0x0d28e9Bd340e48370475553D21Bd0A95c9a60F92",
            BigInteger.ONE,
            order.id,
            order.currency,
            order.price,
            order.token?.tokenId
        )
        assertThat(response.data).hasSize(1)
    }
}
