package com.rarible.x2y2.starter

import com.rarible.x2y2.client.X2Y2ApiClient
import com.rarible.x2y2.client.model.Event
import com.rarible.x2y2.client.model.EventType
import com.rarible.x2y2.client.model.Order
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import scalether.domain.Address
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * To run this test put real x2y2 api key to env variable with name RARIBLE_X2Y2_API_KEY
 */
@SpringBootTest(
    classes = [X2Y2ClientAutoconfiguration::class]
)
@ActiveProfiles("test")
@Import(TestConfiguration::class)
@EnabledIfEnvironmentVariable(
    named = "RARIBLE_X2Y2_API_KEY",
    matches = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}\$"
)
class X2Y2ClientIntegrationTest {

    @Autowired
    private lateinit var client: X2Y2ApiClient

    @Test
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
    internal fun `should sign order`() = runBlocking {
        val orders = client.orders(
            contract = Address.apply("0x259bF444f0bFE8Af20b6097cf8D32A85526B03a4"),
            tokenId = BigInteger.valueOf(999)
        )

        assertTrue(orders.data.isNotEmpty())

        val order = orders.data.first()
        val response = client.fetchOrderSign(
            "0x0d28e9Bd340e48370475553D21Bd0A95c9a60F92",
            BigInteger.ONE,
            order.id,
            order.currency,
            order.price,
            order.token?.tokenId
        )

        assertEquals(1, response.data.size)
    }
}
