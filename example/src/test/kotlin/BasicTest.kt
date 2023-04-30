import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should contain same`
import org.junit.jupiter.api.Test
import uk.gibby.dsl.core.insert
import uk.gibby.dsl.types.`*`
import uk.gibby.dsl.types.createContent
import uk.gibby.dsl.types.eq
import uk.gibby.dsl.types.`o-→`

class BasicTest: RootTest() {
    @Test
    fun createTest() {
        val testUser = User("testuser123", "testPassword123", listOf())
        runBlocking {
            db.transaction { user.createContent(testUser) }
        } `should contain same` listOf(testUser)
    }

    @Test
    fun updateTest() {
        val testUser = User("testuser123", "testPassword123", listOf())
        runBlocking {
            db.transaction {
                +user.createContent(testUser)
                user.update { password setAs "NewPassword" }
            }
        } `should contain same` listOf(User("testuser123", "NewPassword", listOf()))
    }

    @Test
    fun `Basic 'RELATE' test`() {
        runBlocking {
            db.transaction {
                +user.createContent(User("testing", "testPass", listOf()))
                +product.createContent(Product("TestProduct"))
                val selectedUsers by user.selectAll { where(username eq "testing") }
                val selectedProducts by product.selectAll()
                +relate(selectedUsers, has, selectedProducts, Has("also some data"))
                user.select { `o-→`(has).`o-→`(product).`*` }
            }
        } `should contain same` listOf(listOf(Product("TestProduct")))
    }

    @Test
    fun `Basic 'INSERT' test`() {
        runBlocking {
            db.transaction {
                product.insert(Product("apple"), Product("orange"))
            }
        } `should contain same` listOf(Product("apple"), Product("orange"))
    }

    @Test
    fun `'INSERT' from 'SELECT'`() {
        runBlocking {
            db.transaction {
                product.insert(Product("apple"), Product("orange"))
            }
        } `should contain same` listOf(Product("apple"), Product("orange"))
    }
}