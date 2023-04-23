import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.model.auth.RootAuth

class UserTest: RootTest(){
    init {
        runBlocking {
            db.define(NewSchema)
            db.invalidate()
        }
    }

    @Test
    fun signUpTest() {
        runBlocking {
            db.signUp("test", "test", LoggedInScope, UserCredentials("testuser", "testpass"))
            db.transaction { user.selectAll() }
        }
    }
}

class SimpleTest{
    @Test
    fun signUpTest() {
        runBlocking {
            val db = DatabaseConnection("localhost")
            db.connect()
            db.signUp("test", "test", LoggedInScope, UserCredentials("testuser", "testpass"))
        }
    }
}