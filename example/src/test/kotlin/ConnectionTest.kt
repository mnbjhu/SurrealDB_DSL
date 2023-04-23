import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.model.auth.RootAuth

class ConnectionTest {

    private val db = DatabaseConnection("localhost")

    @Test
    fun signInAsRoot() {
        val response = runBlocking {
            db.connect()
            db.signInAsRoot(RootAuth("root", "root"))
        }
        response `should be equal to` null
    }

    @Test
    fun defineNamespace() {
        runBlocking {
            db.connect()
            db.signInAsRoot(RootAuth("root", "root"))
            db.removeNamespace("test_namespace")
            db.defineNamespace("test_namespace")
        }
    }

    @Test
    fun createDatabase() {
        runBlocking {
            db.connect()
            db.signInAsRoot(RootAuth("root", "root"))
            db.removeNamespace("test_namespace")
            db.defineNamespace("test_namespace")
            db.defineDatabase("test_namespace", "test_database")
            db.use("test_namespace", "test_database")
        }
    }

    @Test
    fun setSchema() {
        runBlocking {
            db.connect()
            db.signInAsRoot(RootAuth("root", "root"))
            db.removeNamespace("test_namespace")
            db.defineNamespace("test_namespace")
            db.defineDatabase("test_namespace", "test_database")
            db.use("test_namespace", "test_database")
            db.define(NewSchema)
        }
    }

    @Test
    fun logout() {
        runBlocking {
            db.connect()
            db.signInAsRoot(RootAuth("root", "root"))
            db.removeNamespace("test_namespace")
            db.defineNamespace("test_namespace")
            db.invalidate()
            db.removeNamespace("test_namespace")
        }
    }
}
object NewSchema: TypedSchema() {
    override val scopes = listOf(LoggedInScope)
}
