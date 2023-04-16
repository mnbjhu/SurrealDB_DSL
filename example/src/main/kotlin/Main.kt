import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import uk.gibby.dsl.*

@Record
@Serializable
data class Test(
    val myString: String,
    val myBool: Boolean,
    val myList: List<String>,
    val myNullableString: String?,
    val others: List<Linked<Other>>
)

@Record
@Serializable
data class Other(val data: String)

@Record
@Serializable
data class Other1(val data: String, val thing: MyThing)
@Object
@Serializable
data class MyThing(
    val myString: String,
    val myBool: Boolean,
    val myList: List<String>,
    val myNullableString: String?
)
fun main(){
    runBlocking {
        val db = DatabaseConnection("localhost", 8000).apply { connect() }
        db.signInAsRoot(RootAuth("root", "root"))
        db.use("test", "test")
        db.transaction {
            +UserTable.delete()
            +ProductTable.delete()
            +UserTable.create(User("something", "other thing", listOf()))
            +ProductTable.create(Product("a product"))
            +UserTable.update {
                products setAs ProductTable.select { id }
            }
            +UserTable.select {
                fetch(products)
                products
            }
            +Other1Table.create(Other1("some", MyThing("other", true, listOf("abc", "123"), null)))
            +Other1Table.select {
                thing.myList
            }
            Other1Table.select {
                thing.myNullableString
            }
        }.forEach { println(it) }
    }
}


@Record
@Serializable
data class Product(val name: String)

@Record
@Serializable
data class User(val username: String, val password: String, val products: List<Linked<Product>>)

