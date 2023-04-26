import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

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
            db.transaction { user.selectAll() } `should be equal to` listOf()
        } `should be equal to` listOf()
    }
}