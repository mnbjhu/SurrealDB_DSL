import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import uk.gibby.dsl.annotation.Object
import uk.gibby.dsl.annotation.Table
import uk.gibby.dsl.annotation.Relation
import uk.gibby.dsl.core.insert
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.functions.*
import uk.gibby.dsl.model.auth.RootAuth
import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.*
import kotlin.time.Duration

@Table
data class Test(
    val myString: String,
    val myBool: Boolean,
    val myList: List<String>,
    val myNullableString: String?,
    val others: List<Linked<Other>>
)

@Table
data class Other(val data: String)

@Table
data class Other1(val data: String, val thing: MyThing)
@Object
data class MyThing(
    val myString: String,
    val myBool: Boolean,
    val myList: List<String>,
    val myNullableString: String?
)

@Table
data class NewType(val myDouble: Double, val myLong: Long, val myDuration: Duration, val myDateTime: Instant)


@Relation<User, Product>
data class NewThing(val data: String)


fun main(){
    runBlocking {
        val db = DatabaseConnection("localhost", 8000).apply { connect() }
        db.signInAsRoot(RootAuth("root", "root"))
        db.use("test", "test")
        db.transaction {
            +user.delete()
            +product.delete {
                where(name eq name)
            }
            +user.create(User("something", "other thing", listOf()))
            +product.create(Product("a product"))
            +user.update {
                products setAs product.select { id }
            }
            +user.select {
                fetch(products)
                products
            }
            +other1.create(Other1("some", MyThing("other", true, listOf("abc", "123"), null)))
            +other1.select {
                Time.now()
            }
            +newType.create(NewType(1.0, 10, Duration.parse("2h"), Clock.System.now()))
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
            +user["james"].create(User("mnbjhu", "password123", listOf()))
            +user["james"].selectAll()
            product.insert(Product("apple"), Product("orange"), Product("pear"))
        }.forEach { println(it) }
    }
}


@Table
data class Product(val name: String)

@Table
data class User(
    val username: String,
    val password: String,
    val products: List<Linked<Product>>
)

@Relation<User, Product>
class Has(val data: String)
