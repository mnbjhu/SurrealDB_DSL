import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import uk.gibby.dsl.core.PermissionType
import uk.gibby.dsl.core.PermissionType.*
import uk.gibby.dsl.core.Schema
import uk.gibby.dsl.core.TableDefinition
import uk.gibby.dsl.core.getDefinition
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.functions.length
import uk.gibby.dsl.functions.lessThan
import uk.gibby.dsl.model.auth.RootAuth
import uk.gibby.dsl.types.BooleanType
import uk.gibby.dsl.types.BooleanType.Companion.FALSE
import uk.gibby.dsl.types.eq
import kotlin.test.assertFails

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
            assertFails { db.removeNamespace("test_namespace") }
        }
    }
}
object NewSchema: Schema() {
    override val scopes = listOf(LoggedInScope)
    override val tables: List<TableDefinition> = listOf(
        user.getDefinition(),
        product.getDefinition()
    )
    override fun SchemaScope.configure() {
        user.permissions(LoggedInScope, Create, Select){
            username eq "mnbjhu"
        }
        user.configureFields {
            username.permissions(LoggedInScope, Select){ auth ->
                FALSE
            }
        }
    }
}
