package com.rarible.x2y2.starter

import java.net.URI
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

internal const val RARIBLE_X2Y2 = "rarible.x2y2"

@ConstructorBinding
@ConfigurationProperties(RARIBLE_X2Y2)
data class X2Y2ClientProperties(
    val endpoint: URI?,
    val apiKey: String,
    val proxy: URI?,
)
