package e2e

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.junit.jupiter.api.BeforeEach
import schema.SurrealTvSchema
import schema.UserCredentials
import schema.UserDetails
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.model.auth.RootAuth


abstract class DatabaseTest {
    protected val testUserCredentials = UserCredentials("mnbjhu", "testpass")
    protected val testUserDetails = UserDetails("James", "Gibson", Instant.parse("1999-03-31T00:00:00Z"), "james.gibson@test.com", "441234567890")
    protected val databaseName = this@DatabaseTest::class.simpleName.toString()
    protected val db = DatabaseConnection("localhost")
    open suspend fun setupDatabase() {
        runBlocking {
            db.connect()
            db.signInAsRoot(testRootAuth)
            db.removeDatabase(namespaceName, databaseName)
            db.defineDatabase(namespaceName, databaseName)
            db.use(namespaceName, databaseName)
            db.define(SurrealTvSchema)
        }
    }

    @BeforeEach
    fun setup(){
        runBlocking {
            setupDatabase()
        }
    }
    companion object {
        val testRootAuth = RootAuth("root", "root")
        const val namespaceName = "e2e_tests"
    }
}

