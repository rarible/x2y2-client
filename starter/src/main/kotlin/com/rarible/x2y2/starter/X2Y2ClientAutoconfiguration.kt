package com.rarible.x2y2.starter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rarible.x2y2.client.X2Y2ApiClient
import io.netty.channel.ChannelOption
import io.netty.channel.epoll.EpollChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import java.time.Duration
import java.util.concurrent.TimeUnit
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import reactor.netty.transport.ProxyProvider

@Configuration
@EnableConfigurationProperties(X2Y2ClientProperties::class)
class X2Y2ClientAutoconfiguration(
    private val properties: X2Y2ClientProperties
) {

    @Bean
    @ConditionalOnMissingBean(name = ["x2y2Mapper"])
    fun x2y2Mapper(): ObjectMapper {
        return jacksonObjectMapper().registerModule(JavaTimeModule())
    }

    @Bean
    @ConditionalOnMissingBean(name = ["x2y2ClientConnector"])
    fun x2y2ClientConnector(): ClientHttpConnector {
        val provider = ConnectionProvider.builder("x2y2-connection-provider")
            .maxConnections(50)
            .pendingAcquireMaxCount(-1)
            .maxIdleTime(DEFAULT_TIMEOUT)
            .maxLifeTime(DEFAULT_TIMEOUT)
            .lifo()
            .build()

        val client = HttpClient.create(provider)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .option(EpollChannelOption.TCP_KEEPCNT, 8)
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, DEFAULT_TIMEOUT_MILLIS.toInt())
            .doOnConnected { connection ->
                connection.addHandlerLast(ReadTimeoutHandler(DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS))
                connection.addHandlerLast(WriteTimeoutHandler(DEFAULT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS))
            }
            .responseTimeout(DEFAULT_TIMEOUT)

        if (properties.proxy != null) {
            client
                .proxy { option ->
                    val userInfo = properties.proxy.userInfo.split(":")
                    option.type(ProxyProvider.Proxy.HTTP)
                        .host(properties.proxy.host)
                        .username(userInfo[0])
                        .password { userInfo[1] }
                        .port(properties.proxy.port)
                }
        }

        return ReactorClientHttpConnector(client)
    }

    @Bean("x2y2WebClient")
    @ConditionalOnMissingBean(name = ["x2y2WebClient"])
    fun x2y2WebClient(
        @Qualifier("x2y2Mapper") mapper: ObjectMapper,
        @Qualifier("x2y2ClientConnector") connector: ClientHttpConnector
    ): WebClient {
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
        return builder.build()
    }

    @Bean
    fun x2y2ApiClient(@Qualifier("x2y2WebClient") webClient: WebClient): X2Y2ApiClient {
        return X2Y2ApiClient(webClient)
    }

    companion object {
        private val DEFAULT_TIMEOUT = Duration.ofSeconds(60L)
        private val DEFAULT_TIMEOUT_MILLIS = DEFAULT_TIMEOUT.toMillis()
    }
}
