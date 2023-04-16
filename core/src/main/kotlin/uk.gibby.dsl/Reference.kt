package uk.gibby.dsl

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

val userTable = Table("user_table", UserRecord("_"))

interface Reference<T> {
    fun getReference(): String
    fun createReference(ref: String): Reference<T>
}

inline fun <reified T, U: RecordType<T>> Table<T, U>.create(content: T): ListType<T, U> =
    ListType(recordType, "CREATE $name CONTENT ${surrealJson.encodeToString(content)}")

data class Table<T, U: RecordType<T>>(val name: String, val recordType: U) {
    fun delete(deleteScope: context(FilterScope) U.() -> Unit = {}): NullableType<String, StringType> {
        val filter = FilterScopeImpl()
        filter.deleteScope(recordType)
        return NullableType("DELETE FROM $name ${filter.getFilterString()}")
    }

    fun selectAll(selectScope: context(FilterScope) U.() -> Unit = {}): ListType<T, U> {
        val filter = FilterScopeImpl()
        filter.selectScope(recordType)
        return ListType(recordType, "SELECT * FROM $name ${filter.getFilterString()}")
    }

    fun <r, R: Reference<r>>select(projection: context(FilterScope) U.() -> R): ListType<r, R> {
        val filter = FilterScopeImpl()
        val toSelect = with(filter) {
            projection(recordType)
        }
        return ListType(toSelect, "SELECT ${toSelect.getReference()} AS col1 FROM $name ${filter.getFilterString()}")
    }

    fun update(updateScope: context(SetScope, FilterScope) U.() -> Unit): ListType<T, U> {
        val setScope = SetScope()
        val filterScope = FilterScopeImpl()
                updateScope(setScope, filterScope, recordType)

        return ListType(recordType, "UPDATE $name ${setScope.getSetString()} ${filterScope.getFilterString()}")
    }
}

open class SetScope {
    private var text = "SET "
    fun _addParam(paramText: String){
        text += paramText
    }
    infix fun <T, U: Reference<T>>U.setAs(value: U){
        text += "${getReference()} = ${value.getReference()},"
    }

    inline infix fun <reified T> Reference<T>.setAs(value: T) {
        _addParam("${getReference()} = ${surrealJson.encodeToString(value)},")
    }
    fun getSetString() = text.dropLast(1)
}


interface RecordType<T>: ObjectType<T> {
    val id: RecordLink<T, RecordType<T>>
    override fun <T, U: Reference<T>>attrOf(name: String, type: U): U {
        return type.createReference(name) as U
    }
    fun <T, U: RecordType<T>>U.id() = attrOf("id", RecordLink<T, U>("_"))
}


interface ObjectType<T>: Reference<T> {
    fun <T, U: Reference<T>>attrOf(name: String, type: U): U {
        return type.createReference(this@ObjectType.getReference() + "." + name) as U
    }
}

sealed class Linked<T> {
    abstract val id: String
    data class Reference<T>(override val id: String): Linked<T>()
    data class Actual<T>(override val id: String, val result: T): Linked<T>()
}

@JvmInline
value class RecordLink<T, out U: RecordType<T>>(private val reference: String): Reference<Linked<T>> {
    override fun getReference() = reference
    override fun createReference(ref: String): Reference<Linked<T>> = RecordLink<T, U>(ref)
}

val stringType = StringType("_")

@JvmInline
value class StringType(private val reference: String): Reference<String> {
    override fun getReference(): String = reference
    override fun createReference(ref: String): Reference<String> = StringType(ref)
}


val booleanType = BooleanType("_")

@JvmInline
value class BooleanType(private val reference: String): Reference<Boolean> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = BooleanType(ref)
}


@JvmInline
value class NullableType<T, U: Reference<T>>(private val reference: String): Reference<T?> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = NullableType<T, U>(ref)
}

@Serializable
data class TestClass(val inner: String)

@Serializable
data class User(val username: String, val password: String)

@JvmInline
value class TestType(private val reference: String): ObjectType<TestClass> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = TestType(ref)
    val inner get() = attrOf("inner", stringType)
}

val testType = TestType("_")

@JvmInline
value class UserRecord(private val reference: String): RecordType<User> {
    override val id: RecordLink<User, UserRecord> get() = id()
    inline val username get() = attrOf("username", stringType)
    inline val password get() = attrOf("password", stringType)
    inline val data get() = attrOf("data", testType)

    override fun getReference(): String = reference
    override fun createReference(ref: String) = UserRecord(ref)
}

data class ListType<T, U: Reference<T>>(private val inner: U, private val reference: String): Reference<List<T>> {
    override fun getReference() = reference
    override fun createReference(ref: String) = ListType(inner, ref)
}


fun <T, U: Reference<T>>list(inner: U) = ListType(inner, "_")
fun <T, U: Reference<T>>nullable(inner: U) = NullableType<T, U>("_")



class FilterScopeImpl: FilterScope {
    private var where: String? = null
    override fun getFilterString(): String {
        var r = ""
        if(where != null) r += "WHERE $where"
        return r

    }

    override fun where(condition: BooleanType) {
        where = condition.getReference()
    }
}

interface FilterScope {
    fun getFilterString(): String
    fun where(condition: BooleanType)
}

infix fun <T, U: Reference<T>>U.eq(other: U) = BooleanType("(${getReference()} == ${other.getReference()})")
inline infix fun <reified T> Reference<T>.eq(other: T) = BooleanType("(${getReference()} == ${surrealJson.encodeToString(other)})")

