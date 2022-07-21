package com.rarible.x2y2.client

import com.rarible.x2y2.client.model.ApiListResponse
import com.rarible.x2y2.client.model.Contract
import com.rarible.x2y2.client.model.ContractStats
import com.rarible.x2y2.starter.X2Y2ClientAutoconfiguration
import java.math.BigInteger
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(
    properties = [
        "rarible.x2y2.apiUrl = http://localhost:9999",
        "rarible.x2y2.apiKey = 'custom-api-key'"
    ],
    classes = [X2Y2ClientAutoconfiguration::class]
)
@ActiveProfiles("test")
@Import(TestConfiguration::class)
class X2Y2ClientTest {

    private lateinit var mockBackend: MockWebServer

    @Autowired
    private lateinit var client: X2Y2WebClient

    private val orders: ClassPathResource = ClassPathResource("json/orders.json")

    private val listEvents: ClassPathResource = ClassPathResource("json/events-list.json")

    private val saleEvents: ClassPathResource = ClassPathResource("json/events-sale.json")

    private val cancelListEvents: ClassPathResource = ClassPathResource("json/events-cancel_listing.json")

    private val offerListingEvents: ClassPathResource = ClassPathResource("json/events-offer_listing.json")

    private val contractAnswer: ClassPathResource = ClassPathResource("json/contract.json")

    private val contractStatsAnswer: ClassPathResource = ClassPathResource("json/contract_stats.json")


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
        }
    }

    @Test
    internal fun `should read list events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(listEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = "list")
            assertListAnswer(answer, "WzE2NDM5ODcwOTNd")
        }
    }

    @Test
    internal fun `should read sale events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(saleEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = "sale")
            assertListAnswer(answer, "WzE2NDQwNTQzMTRd")
        }
    }

    @Test
    internal fun `should read cancel listing events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(cancelListEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = "cancel_listing")
            assertListAnswer(answer, "WzE2NDQ1MTAzODNd")
        }
    }

    @Test
    internal fun `should read offer listing events`() {
        runBlocking {
            mockBackend.enqueue(
                MockResponse().setBody(offerListingEvents.file.readText()).addHeader("Content-Type", "application/json")
            )
            val answer = client.events(type = "offer_listing")
            assertListAnswer(answer, "WzE2NDM5ODcwOTNd")
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
            assertThat(answer.data.contract).isEqualTo("0x52c5dd8a4ffb5a24121d76c222014697b5e6cc6e")
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

    private fun assertListAnswer(answer: ApiListResponse<*>, expectedNext: String) {
        assertThat(answer).isNotNull
        assertThat(answer.next).isNotNull
        assertThat(answer.next).isEqualTo(expectedNext)
        assertThat(answer.success).isTrue
        assertThat(answer.data).isNotEmpty
        assertThat(answer.data.size).isEqualTo(20)
    }

    @AfterEach
    internal fun tearDown() {
        mockBackend.shutdown()
    }
}
