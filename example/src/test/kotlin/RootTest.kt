import kotlinx.coroutines.runBlocking
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.model.auth.RootAuth

abstract class RootTest {

    protected val db = DatabaseConnection("localhost", 8000)
    init {
        runBlocking {
            db.connect()
            db.signInAsRoot(RootAuth("root", "root"))
            db.removeNamespace("test")
            db.defineNamespace("test")
            db.defineDatabase("test", "test")
            db.use("test", "test")
            db.transaction { user.delete() }
            db.transaction { product.delete() }
        }
    }
}