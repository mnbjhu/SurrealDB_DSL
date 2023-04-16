package uk.gibby.surrealdb.core

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*

val Client = HttpClient(CIO) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json { ignoreUnknownKeys = true })
    }
}

@Serializable
data class RpcRequest(val id: String, val method: String, val params: JsonArray)

@Serializable
data class RpcResponse(val id: String, val result: JsonElement)

class DatabaseConnection(val host: String, val port: Int, val ns: String, val db: String) {

    private var count = 0L
    private var connection: DefaultClientWebSocketSession? = null
    private val requests = ConcurrentMap<String, Channel<JsonElement>>()
    private val context = CoroutineScope(Dispatchers.IO)
    suspend fun connect() {
        connection = Client.webSocketSession(method = HttpMethod.Get, host = host, port = port, path = "/rpc").also {
            context.launch {
                it.incoming.receiveAsFlow().collect {
                    it as Frame.Text
                    println(it.readText())
                    val response = surrealJson.decodeFromString<RpcResponse>(it.readText())
                    (requests[response.id] ?: throw Exception("Response id: ${response.id} doesn't match any sent")).send(response.result)
                    requests.remove(response.id)
                }
            }
        }
    }

    private suspend fun sendRequest(method: String, params: JsonArray): JsonElement {
        val id = count++.toString()
        val request = RpcRequest(id, method, params)
        val channel = Channel<JsonElement>(1)
        requests[id] = channel
        (connection ?: throw Exception("SurrealDB: Websocket not connected")).sendSerialized(request)
        return channel.receive()
    }

    suspend fun signInAsRoot(auth: RootAuth): String? {
        val result = sendRequest("signin", surrealJson.encodeToJsonElement(listOf(auth)) as JsonArray)
        return surrealJson.decodeFromJsonElement(result)
    }

    suspend fun query(queryText: String): JsonElement {
        val result = sendRequest("query", buildJsonArray { add(queryText) })
        return surrealJson.decodeFromJsonElement<QueryResponse>((result as JsonArray).last()).result
    }

    suspend fun use(ns: String, db: String): JsonElement {
        val result = sendRequest("use", buildJsonArray { add(ns); add(db) })
        return surrealJson.decodeFromJsonElement(result)
    }

    suspend inline fun <reified T>transaction(crossinline scope: TransactionScope.() -> Reference<T>): T {
        val transaction = TransactionScope()
        val result = transaction.scope()
        with(transaction) { +result }
        val queryText = transaction.getQueryText()
        val rawResponse = query(queryText)
        val response = try {
            buildJsonArray { (rawResponse as JsonArray).forEach{ add(surrealJson.decodeFromJsonElement<Row1>(it).col1) } }
        } catch (e: Exception) {
            rawResponse
        }

        return surrealJson.decodeFromJsonElement(response)
    }
}

class TransactionScope {
    private var generated: String = "BEGIN TRANSACTION;"
    operator fun Reference<*>.unaryPlus(){
        generated += getReference()
        generated += ";"
    }
    fun getQueryText() = generated + "COMMIT TRANSACTION;"
}

class UpdateScope: SetScope(), FilterScope {
    private var condition: String? = null
    override fun getFilterString() =
        if(condition == null) "" else " WHERE $condition"

    override fun where(condition: BooleanType) {
        this.condition = condition.getReference()
    }
}

@Serializable
data class QueryResponse(val time: String, val status: String, val result: JsonElement)

@Serializable
data class RootAuth(val user: String, val pass: String)

@Serializable
data class Row1(val col1: JsonElement)

val surrealJson = Json {
    ignoreUnknownKeys = true
}