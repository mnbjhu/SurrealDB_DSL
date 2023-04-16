package uk.gibby.dsl.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class QueryResponse(val time: String, val status: String, val result: JsonElement)