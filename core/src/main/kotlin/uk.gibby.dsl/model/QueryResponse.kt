package uk.gibby.dsl.model

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class QueryResponse {
    abstract val time: String
    @Serializable
    @SerialName("OK")
    class Success(override val time: String, val result: JsonElement): QueryResponse()

    @Serializable
    @SerialName("ERR")
    class Error(override val time: String, val detail: String): QueryResponse()
}