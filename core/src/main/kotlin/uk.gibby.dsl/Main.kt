package uk.gibby.dsl

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main(){
    val result: ServiceResult<String> = ServiceResult.Success("test")
    println(Json.encodeToString(result))
    /*
    val connection = DatabaseConnection(
        host = "localhost",
        port = 8000,
    )
    runBlocking {
        connection.connect()
        connection.signInAsRoot(RootAuth("root", "root"))
        connection.use("test", "test")
        measureTimeMillis {
            (1..1000)
                .map {
                    async {
                        connection.transaction { userTable.create(User("Test", "Test123")) }
                    }
                }
                .awaitAll()
        }.also { println(it) }
        connection.transaction {
            userTable.select {
                where(username eq "Test")
                username
            }
        }
    }

     */
}