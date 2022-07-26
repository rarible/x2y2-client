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

            Assertions.assertThat(orders.size).isEqualTo(expectedCount)
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
}
