package com.rarible.x2y2.starter

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rarible.x2y2.client.X2Y2WebClient
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(X2Y2ClientProperties::class)
class X2Y2ClientAutoconfiguration(
    private val properties: X2Y2ClientProperties
) {
    @Bean
    fun x2y2WebClient(): X2Y2WebClient {
        val mapper = jacksonObjectMapper().registerModule(JavaTimeModule())
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs {
                it.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper))
                it.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(mapper))
            }.build()
        val builder = WebClient.builder()
        builder.baseUrl("${properties.apiUrl}")
            .defaultHeader("X-API-KEY", properties.apiKey)
            .defaultHeader("Content-Type", "application/json")
            .exchangeStrategies(exchangeStrategies)
        return X2Y2WebClient(builder.build())
    }
}
