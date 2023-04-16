import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import uk.gibby.dsl.*

@Record
@Serializable
data class Test(val myString: String, val myBool: Boolean, val myList: List<String>, val myNullableString: String?)

fun main(){
    val testTable = Table("test_table", TestRecord("_"))
    runBlocking {
        val db = DatabaseConnection("localhost", 8000).apply { connect() }
        db.signInAsRoot(RootAuth("root", "root"))
        db.use("test", "test")
        db.transaction {
            +userTable.create(User("something", "other thing", listOf()))
            +productTable.create(Product("a product"))
            +userTable.update {
                products setAs listOf<>()
            }
            testTable.selectAll {

            }
        }
    }
}


val userTable = Table("user_table", UserRecord("_"))
val productTable = Table("product_table", ProductRecord("_"))
@Serializable
data class Product(val name: String)

@Serializable
data class User(val username: String, val password: String, val products: List<Linked<Product>>)
@JvmInline
value class ProductRecord(private val reference: String): RecordType<Product> {
    override val id: RecordLink<Product, ProductRecord> get() = id()
    inline val name get() = attrOf("name", stringType)

    override fun getReference(): String = reference
    override fun createReference(ref: String) = ProductRecord(ref)
}
@JvmInline
value class UserRecord(private val reference: String): RecordType<User> {
    override val id: RecordLink<User, UserRecord> get() = id()
    inline val username get() = attrOf("username", stringType)
    inline val password get() = attrOf("password", stringType)
    inline val data get() = attrOf("data", testType)
    inline val products get() = attrOf("products", list(linked(productTable)))


    override fun getReference(): String = reference
    override fun createReference(ref: String) = UserRecord(ref)
}

