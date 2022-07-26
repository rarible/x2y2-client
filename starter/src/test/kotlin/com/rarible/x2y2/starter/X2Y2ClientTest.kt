package com.rarible.x2y2.starter

import com.rarible.x2y2.client.X2Y2ApiClient
import com.rarible.x2y2.client.model.ApiListResponse
import com.rarible.x2y2.client.model.Contract
import com.rarible.x2y2.client.model.ContractStats
import com.rarible.x2y2.client.model.EVENTS_ENDPOINT
import com.rarible.x2y2.client.model.EVENTS_MAX_LIMIT
import com.rarible.x2y2.client.model.EventType
import com.rarible.x2y2.client.model.OFFERS_ENDPOINT
import com.rarible.x2y2.client.model.ORDERS_ENDPOINT
import java.math.BigInteger
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles
import scalether.domain.Address

@SpringBootTest(
    properties = [
        "rarible.x2y2.apiUrl = http://localhost:9999",
        "rarible.x2y2.apiKey = custom-api-key"
    ],
    classes = [X2Y2ClientAutoconfiguration::class]
)
@ActiveProfiles("test")
@Import(TestConfiguration::class)
class X2Y2ClientTest {

    private lateinit var mockBackend: MockWebServer

    @Autowired
    private lateinit var client: X2Y2ApiClient

    private val orders: ClassPathResource = ClassPathResource("json/orders.json")

    private val offers: ClassPathResource = ClassPathResource("json/offers.json")

    private val listEvents: ClassPathResource = ClassPathResource("json/events-list.json")

    private val saleEvents: ClassPathResource = ClassPathResource("json/events-sale.json")

    private val cancelListEvents: ClassPathResource = ClassPathResource("json/events-cancel_listing.json")

    private val offerListingEvents: ClassPathResource = ClassPathResource("json/events-offer_listing.json")

    private val contractAnswer: ClassPathResource = ClassPathResource("json/contract.json")

    private val contractStatsAnswer: ClassPathResource = ClassPathResource("json/contract_stats.json")

    private val expectedHeaders = Headers.headersOf("X-API-KEY", "custom-api-key", "Content-Type", "application/json")


    @BeforeEach
    internal fun setUp() {
        mockBackend = MockWebServer()
        mockBackend.start(9999)
    }

    @Test
    internal fun `should read orders`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(orders.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.orders()
            assertListAnswer(answer, "WzE2NDQxNTk0NTUwMDBd")
            val request = mockBackend.takeRequest()

            assertThat(request.method).isEqualTo("GET")
            assertThat(request.headers.toList()).containsAll(expectedHeaders.toList())
            val url = request.requestUrl!!
            assertThat(url.encodedPath).startsWith(ORDERS_ENDPOINT)
        }
    }

    @Test
    internal fun `should read offers`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(offers.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.offers(contract = Address.apply("0x6d19568A959FCB4211852F6472d3df7b67C6Cd54"))
            assertListAnswer(answer, null, 1)
            val request = mockBackend.takeRequest()

            assertThat(request.method).isEqualTo("GET")
            assertThat(request.headers.toList()).containsAll(expectedHeaders.toList())
            val url = request.requestUrl!!
            assertThat(url.encodedPath).startsWith(OFFERS_ENDPOINT)
            assertThat(url.queryParameter("contract")).isEqualTo("0x6d19568a959fcb4211852f6472d3df7b67c6cd54")
        }
    }

    @Test
    internal fun `should fail on orders bad params`() {
        val message = "Contract and token ID must be both defined or both undefined!"
        runBlocking {
            assertThrows<IllegalArgumentException>(message) {
                client.orders(tokenId = BigInteger.ZERO)
            }
            assertThrows<IllegalArgumentException>(message) {
                client.orders(contract = Address.ZERO())
            }
        }
    }

    @Test
    internal fun `should fail on offer bad params`() {
        runBlocking {
            assertThrows<IllegalArgumentException>("Maker or contract must be defined!") {
                client.offers()
            }

            assertThrows<IllegalArgumentException>("Contract must be defined if token ID is defined!") {
                client.offers(tokenId = BigInteger.ONE)
            }
        }
    }

    @Test
    internal fun `should read list events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(listEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = EventType.LIST)
            assertListAnswer(answer, "WzE2NDM5ODcwOTNd")
            val request = mockBackend.takeRequest()

            assertEventsRequest(request, EventType.LIST)
        }
    }


    @Test
    internal fun `should read sale events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(saleEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = EventType.SALE)
            assertListAnswer(answer, "WzE2NDQwNTQzMTRd")
            assertEventsRequest(mockBackend.takeRequest(), EventType.SALE)
        }
    }

    @Test
    internal fun `should read cancel listing events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(cancelListEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = EventType.CANCEL_LISTING)
            assertListAnswer(answer, "WzE2NDQ1MTAzODNd")
            assertEventsRequest(mockBackend.takeRequest(), EventType.CANCEL_LISTING)
        }
    }

    @Test
    internal fun `should read offer listing events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(offerListingEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = EventType.OFFER_LISTING)
            assertListAnswer(answer, "WzE2NDM5ODcwOTNd")
            assertEventsRequest(mockBackend.takeRequest(), EventType.OFFER_LISTING)
        }
    }

    @Test
    internal fun `should read a contract`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(contractAnswer.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.contract("0x52c5dd8a4ffb5a24121d76c222014697b5e6cc6e")
            assertThat(answer).isNotNull
            assertThat(answer.success).isTrue
            assertThat(answer.next).isNull()
            assertThat(answer.data).isExactlyInstanceOf(Contract::class.java)
            assertThat(answer.data.contract).isEqualTo(Address.apply("0x52c5dd8a4ffb5a24121d76c222014697b5e6cc6e"))
        }
    }

    @Test
    internal fun `should read contract stats`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(contractStatsAnswer.file.readText())
                    .addHeader("Content-Type", "application/json")
            )
            val answer = client.contractStats("0x52c5dd8a4ffb5a24121d76c222014697b5e6cc6e")
            assertThat(answer).isNotNull
            assertThat(answer.success).isTrue
            assertThat(answer.next).isNull()
            assertThat(answer.data).isExactlyInstanceOf(ContractStats::class.java)
            assertThat(answer.data.floorPrice).isGreaterThan(BigInteger.ZERO)
        }
    }

    private fun assertListAnswer(answer: ApiListResponse<*>, expectedNext: String?, expectedSize: Int = 20) {
        assertThat(answer).isNotNull
        if (expectedNext == null) {
            assertThat(answer.next).isNull()
        } else {
            assertThat(answer.next).isNotNull
            assertThat(answer.next).isEqualTo(expectedNext)
        }
        assertThat(answer.success).isTrue
        assertThat(answer.data).isNotEmpty
        assertThat(answer.data.size).isEqualTo(expectedSize)
    }

    private fun assertEventsRequest(request: RecordedRequest, expectedType: EventType) {
        assertThat(request.method).isEqualTo("GET")
        assertThat(request.headers.toList()).containsAll(expectedHeaders.toList())
        assertThat(request.requestUrl?.queryParameter("type")).isEqualToIgnoringCase(expectedType.value)
        assertThat(request.requestUrl?.queryParameter("limit")?.toInt()).isEqualTo(EVENTS_MAX_LIMIT)
        assertThat(request.path).contains(EVENTS_ENDPOINT)
    }

    @AfterEach
    internal fun tearDown() {
        mockBackend.shutdown()
    }
}
