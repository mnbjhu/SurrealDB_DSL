package uk.gibby.dsl.driver

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.Json

val Client = HttpClient(CIO) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json { ignoreUnknownKeys = true })
    }
}

val surrealJson = Json {
    ignoreUnknownKeys = true
}