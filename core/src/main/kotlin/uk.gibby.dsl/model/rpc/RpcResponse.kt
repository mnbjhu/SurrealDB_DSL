package uk.gibby.dsl.model.rpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class RpcResponse(val id: String, val result: JsonElement)