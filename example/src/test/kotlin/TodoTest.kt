import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import uk.gibby.dsl.core.insert
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.functions.*
import uk.gibby.dsl.model.auth.RootAuth
import uk.gibby.dsl.types.`*`
import uk.gibby.dsl.types.createContent
import uk.gibby.dsl.types.eq
import uk.gibby.dsl.types.`o-→`
import kotlin.time.Duration

class TodoTest {
    fun main() {
        runBlocking {
            val db = DatabaseConnection("localhost", 8000).apply { connect() }
            db.signInAsRoot(RootAuth("root", "root"))
            db.use("test", "test")
            db.transaction {
                +user.delete()
                +product.delete {
                    where(name eq name)
                }
                +user.createContent(User("something", "other thing", listOf()))
                +product.createContent(Product("a product"))
                +user.update {
                    products setAs product.select { id }
                }
                +user.select {
                    fetch(products)
                    products
                }
                +other1.createContent(Other1("some", MyThing("other", true, listOf("abc", "123"), null)))
                +other1.select {
                    Time.now()
                }
                +newType.createContent(NewType(1.0, 10, Duration.parse("2h"), Clock.System.now()))
                val users by user.selectAll()
                val products by product.selectAll()
                +relate(users, has, products, Has(""))
                +user.select {
                    `o-→` (has).`o-→` (product).`*`
                }
                +user.update {
                    password setAs `if`(username.length() lessThan 8){
                        password + "123"
                    }.`else` {
                        password
                    }
                }
                +user.update {
                    password setAs `if`(username.length() lessThan 8){
                        password + "123"
                    }.`else if`(username.length() eq 9) {
                        password + password
                    }.`else` {
                        password
                    }
                }
                +user["james"].createContent(User("mnbjhu", "password123", listOf()))
                +user["james"].selectAll()
                +product.insert(Product("apple"), Product("orange"), Product("pear"))
                +game.createContent(Game(PGN(
                    "test.Test Event",
                    "localhost",
                    Clock.System.now(),
                    3,
                    "Test Player 1",
                    "Test Player 2",
                    Color.Black
                )))

                user.select {
                    `o-→` (has).`o-→` (product).`*`
                }
            }.forEach { println(it) }
        }
    }



}
