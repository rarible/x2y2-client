package com.rarible.x2y2.starter

import com.rarible.x2y2.client.X2Y2ApiClient
import com.rarible.x2y2.client.model.Event
import com.rarible.x2y2.client.model.EventType
import com.rarible.x2y2.client.model.OperationResult
import com.rarible.x2y2.client.model.Order
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
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
    internal fun `should get sign order error`() = runBlocking<Unit> {
        val response = client.fetchOrderSign(
            "0x47921676A46CcFe3D80b161c7B4DDC8Ed9e716B6",
            BigInteger.ONE,
            BigInteger("22150346"),
            Address.apply("0x0000000000000000000000000000000000000000"),
            BigInteger("500000000000000000"),
            BigInteger("32292934596187112148346015918544186536963932779440027682601542850818403729412"),
        )
        when (response) {
            is OperationResult.Fail -> {
                assertThat(response.error.success).isFalse
                assertThat(response.error.errors.single().code).isEqualTo(2020)
                assertThat(response.error.errors.single().orderId).isEqualTo(BigInteger("22150346"))
            }
            is OperationResult.Success -> {
                fail("Not success result")
            }
        }
    }

    @Test
    @Disabled
    internal fun `should get sign order success`() = runBlocking<Unit> {
        val response = client.fetchOrderSign(
            "0x47921676A46CcFe3D80b161c7B4DDC8Ed9e716B6",
            BigInteger.ONE,
            BigInteger("21996067"),
            Address.apply("0x0000000000000000000000000000000000000000"),
            BigInteger("1000000000000000000"),
            BigInteger("4542"),
        )
        when (response) {
            is OperationResult.Fail -> {
                fail("Not success result")
            }
            is OperationResult.Success -> {
                assertThat(response.result.success).isTrue
                assertThat(response.result.data.single().orderId).isEqualTo("21996067")
            }
        }
    }
}
