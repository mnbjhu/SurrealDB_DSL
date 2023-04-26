import kotlinx.datetime.Instant
import uk.gibby.dsl.annotation.*
import uk.gibby.dsl.model.Linked
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
data class NewType(
    val myDouble: Double,
    val myLong: Long,
    val myDuration: Duration,
    val myDateTime: Instant
)


@Relation<User, Product>
data class NewThing1(val data: String)
@Object
enum class TestEnum {
    First, Second
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




@Object
data class UserCredentials(val username: String, val password: String)
