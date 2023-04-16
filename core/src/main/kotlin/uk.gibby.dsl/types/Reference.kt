package uk.gibby.dsl.types

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.core.Table
import uk.gibby.dsl.driver.surrealJson


interface Reference<T> {
    fun getReference(): String
    fun createReference(ref: String): Reference<T>
}

inline fun <reified T, U: RecordType<T>> Table<T, U>.create(content: T): ListType<T, U> =
    ListType(recordType, "CREATE $name CONTENT ${surrealJson.encodeToString(content)}")





fun <T, U: RecordType<T>, r, R: Reference<r>> ListType<Linked<T>, RecordLink<T, U>>.linked(transform: U.() -> R): ListType<r, R> =
    (inner.inner.createReference("") as U)
        .run { val returned = transform(); ListType(returned, this@linked.getReference() + returned.getReference()) }
fun <T, U: Reference<T>>list(inner: U) = ListType(inner, "_")
fun <T, U: Reference<T>>nullable(inner: U) = NullableType<T, U>("_")


infix fun <T, U: Reference<T>>U.eq(other: U) = BooleanType("(${getReference()} == ${other.getReference()})")
inline infix fun <reified T> Reference<T>.eq(other: T) = BooleanType("(${getReference()} == ${surrealJson.encodeToString(other)})")

