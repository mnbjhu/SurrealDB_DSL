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
            +testTable.create(Test("something", true, listOf("a", "b", "c"), null))
            testTable.selectAll{

            }
        }
    }
}