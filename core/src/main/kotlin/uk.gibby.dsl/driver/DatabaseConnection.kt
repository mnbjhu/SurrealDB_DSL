package uk.gibby.dsl.driver

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.util.collections.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import uk.gibby.dsl.core.Schema
import uk.gibby.dsl.core.Scope
import uk.gibby.dsl.core.Table
import uk.gibby.dsl.model.*
import uk.gibby.dsl.model.auth.RootAuth
import uk.gibby.dsl.model.auth.ScopeAuth
import uk.gibby.dsl.model.rows.Row1
import uk.gibby.dsl.model.rpc.RpcRequest
import uk.gibby.dsl.model.rpc.RpcResponse
import uk.gibby.dsl.scopes.FilterScope
import uk.gibby.dsl.scopes.FilterScopeImpl
import uk.gibby.dsl.scopes.TransactionScope
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.RecordType
import uk.gibby.dsl.types.Reference
import java.util.concurrent.CancellationException

class DatabaseConnection(private val host: String, private val port: Int = 8000) {

    private var count = 0L
    private var connection: DefaultClientWebSocketSession? = null
    private val requests = ConcurrentMap<String, Channel<JsonElement>>()
    private val context = CoroutineScope(Dispatchers.IO)

    suspend fun connect() {
        connection?.cancel()
        connection = Client.webSocketSession(method = HttpMethod.Get, host = host, port = port, path = "/rpc").also {
            context.launch {
                it.incoming.receiveAsFlow().collect {
                    it as Frame.Text
                    println(it.readText())
                    val response = surrealJson.decodeFromString<RpcResponse>(it.readText())
                    val request = requests[response.id]
                    if (request == null) requests.forEach { (_, r) ->  r.cancel(CancellationException("Received a request with an unknown id: ${response.id} body: $response"))}
                    else when(response) {
                        is RpcResponse.Success -> request.send(response.result)
                        is RpcResponse.Error -> request.cancel(CancellationException("SurrealDB responded with an error.${response.error}"))
                    }
                    requests.remove(response.id)
                }
            }
        }
    }

    suspend fun defineNamespace(name: String) {
        query("DEFINE NAMESPACE $name;")
    }

    suspend fun defineDatabase(ns: String, db: String) {
        query("DEFINE NS $ns; USE NS $ns; DEFINE DATABASE $db;")
    }
    suspend fun removeDatabase(ns: String, db: String) {
        query("USE NS $ns; REMOVE DATABASE $db;")
    }
    suspend fun removeNamespace(name: String) {
        query("REMOVE NAMESPACE $name;")
    }

    private suspend fun sendRequest(method: String, params: JsonArray): JsonElement {
        val id = count++.toString()
        val request = RpcRequest(id, method, params).also { println(it) }
        val channel = Channel<JsonElement>(1)
        requests[id] = channel
        (connection ?: throw Exception("SurrealDB: Websocket not connected")).sendSerialized(request)
        return channel.receive()
    }

    suspend fun signInAsRoot(auth: RootAuth): String? {
        val result = sendRequest("signin", surrealJson.encodeToJsonElement(listOf(auth)) as JsonArray)
        return surrealJson.decodeFromJsonElement(result)
    }

    suspend fun <T, U: Reference<T>, c, C: RecordType<c>>signIn(ns: String, db: String, scope: Scope<*, *, T, U, c, C>, auth: JsonElement): String? {
        val result = sendRequest("signin", surrealJson.encodeToJsonElement(listOf(ScopeAuth(ns, db, scope.name, surrealJson.encodeToJsonElement(auth)))) as JsonArray)
        return surrealJson.decodeFromJsonElement(result)
    }
    suspend inline fun <reified T, U: Reference<T>, c, C: RecordType<c>>signIn(ns: String, db: String, scope: Scope<*, *, T, U, c, C>, auth: T): String? {
        return signIn(ns, db, scope, surrealJson.encodeToJsonElement(auth))
    }

    suspend fun <T, U: Reference<T>, c, C: RecordType<c>>signUp(ns: String, db: String, scope: Scope<T, U, *, *, c, C>, auth: JsonElement): String? {
        val result = sendRequest("signup", surrealJson.encodeToJsonElement(listOf(ScopeAuth(ns, db, scope.name, surrealJson.encodeToJsonElement(auth)))) as JsonArray)
        return surrealJson.decodeFromJsonElement(result)
    }
    suspend inline fun <reified T, U: Reference<T>, c, C: RecordType<c>>signUp(ns: String, db: String, scope: Scope<T, U, *, *, c, C>, auth: T): String? {
        return signUp(ns, db, scope, surrealJson.encodeToJsonElement(auth))
    }
    suspend fun query(queryText: String): JsonElement {
        println(queryText)
        val result = sendRequest("query", buildJsonArray { add(queryText) })
        return when (val response = surrealJson.decodeFromJsonElement<QueryResponse>((result as JsonArray).last())) {
            is QueryResponse.Error -> throw Exception("surrealDB returned an error: ${response.detail}")
            is QueryResponse.Success -> response.result
        }
    }

    suspend fun use(ns: String, db: String): JsonElement {
        val result = sendRequest("use", buildJsonArray { add(ns); add(db) })
        return surrealJson.decodeFromJsonElement(result)
    }
    suspend fun define(schema: Schema) {
        query(schema.getDefinitionQuery())
    }
    suspend inline fun <reified T>transaction(crossinline scope: TransactionScope.() -> Reference<T>): T {
        val transaction = TransactionScope()
        val result = transaction.scope()
        with(transaction) { +result }
        val queryText = transaction.getQueryText()
        val rawResponse = query(queryText).also { println(it) }
        return surrealJson.decodeFromJsonElement(rawResponse)
    }

    suspend fun invalidate(){
        sendRequest("invalidate", JsonArray(listOf()))
    }


    suspend fun <T, U: RecordType<T>>liveSelectAll(table: Table<T, U>) {
        query("LIVE SELECT * FROM ${table.name}")
    }
}