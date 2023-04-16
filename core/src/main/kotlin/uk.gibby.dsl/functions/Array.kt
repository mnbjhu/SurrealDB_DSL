package uk.gibby.dsl.functions

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.longType

object Array {
    fun <T, U: Reference<T>>combine(first: ListType<T, U>, second: ListType<T, U>) = first.createReference("array::combine(${first.getReference()},${second.getReference()})")
    fun <T, U: Reference<T>>concat(first: ListType<T, U>, second: ListType<T, U>) = first.createReference("array::concat(${first.getReference()},${second.getReference()})")
    fun <T, U: Reference<T>>difference(first: ListType<T, U>, second: ListType<T, U>) = first.createReference("array::difference(${first.getReference()},${second.getReference()})")
    fun <T, U: Reference<T>>distinct(first: ListType<T, U>) = first.createReference("array::distinct(${first.getReference()})")
    fun <T, U: Reference<T>>intersect(first: ListType<T, U>, second: ListType<T, U>) = first.createReference("array::intersect(${first.getReference()},${second.getReference()})")

    fun <T, U: Reference<T>>combine(first: ListType<T, U>, second: List<T>) = first.createReference("array::combine(${first.getReference()},${surrealJson.encodeToString(second)})")
    fun <T, U: Reference<T>>concat(first: ListType<T, U>, second: List<T>) = first.createReference("array::concat(${first.getReference()},${surrealJson.encodeToString(second)})")
    fun <T, U: Reference<T>>difference(first: ListType<T, U>, second: List<T>) = first.createReference("array::difference(${first.getReference()},${surrealJson.encodeToString(second)})")
    fun <T, U: Reference<T>>intersect(first: ListType<T, U>, second: List<T>) = first.createReference("array::intersect(${first.getReference()},${surrealJson.encodeToString(second)})")
    fun len(array: ListType<*, *>) = longType.createReference("array::len(${array.getReference()})")

    object Sort {
        fun <T, U: Reference<T>>asc(first: ListType<T, U>) = first.createReference("array::sort::acs(${first.getReference()})")
        fun <T, U: Reference<T>>desc(first: ListType<T, U>) = first.createReference("array::sort::desc(${first.getReference()})")
    }
}