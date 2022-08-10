package com.rarible.x2y2.starter

import com.rarible.x2y2.client.X2Y2ApiClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.URI

@Configuration
@EnableConfigurationProperties(X2Y2ClientProperties::class)
class X2Y2ClientAutoconfiguration(
    private val properties: X2Y2ClientProperties
) {
    @Bean
    @ConditionalOnMissingBean(X2Y2ApiClient::class)
    fun x2y2ProtocolClient(): X2Y2ApiClient {
        return X2Y2ApiClient(
            endpoint = properties.endpoint ?: DEFAULT_ENDPOINT,
            apiKey = properties.apiKey,
            proxy = properties.proxy,
        )
    }

    private companion object {
        val DEFAULT_ENDPOINT: URI = URI.create("https://api.x2y2.org")
    }
}
