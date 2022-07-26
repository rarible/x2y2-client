package com.rarible.x2y2.starter

import java.net.URI
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

internal const val RARIBLE_X2Y2 = "rarible.x2y2"
private val DEFAULT_API_URI = URI.create("https://api.x2y2.org")

@ConstructorBinding
@ConfigurationProperties(RARIBLE_X2Y2)
data class X2Y2ClientProperties(
    val apiUrl: URI = DEFAULT_API_URI,
    val apiKey: String,
    val proxy: URI? = null
)
