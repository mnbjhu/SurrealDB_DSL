import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should contain same`
import org.junit.jupiter.api.Test
import uk.gibby.dsl.types.createContent

class BasicTest: RootTest() {
    @Test
    fun createTest() {
        val testUser = User("testuser123", "testPassword123", listOf())
        runBlocking {
            db.transaction {
                user.createContent(testUser)
            }
        } `should contain same` listOf(testUser)
    }

    @Test
    fun updateTest() {
        val testUser = User("testuser123", "testPassword123", listOf())
        runBlocking {
            db.transaction {
                +user.createContent(testUser)
                user.update {
                    password setAs "NewPassword"
                }
            }
        } `should contain same` listOf(User("testuser123", "NewPassword", listOf()))
    }
}