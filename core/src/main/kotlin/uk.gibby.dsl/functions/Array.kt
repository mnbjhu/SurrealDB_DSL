package uk.gibby.dsl.functions

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.booleanType
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


infix fun <T, U: Reference<T>>U.`in`(other: ListType<T, U>) = booleanType.createReference("${getReference()} IN ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`∋`(other: U) = booleanType.createReference("${getReference()} ∋ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`∌`(other: U) = booleanType.createReference("${getReference()} ∌ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`⊇`(other: ListType<T, U>) = booleanType.createReference("${getReference()} ⊇ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`⊃`(other: ListType<T, U>) = booleanType.createReference("${getReference()} ⊃ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`⊅`(other: ListType<T, U>) = booleanType.createReference("${getReference()} ⊅ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`∈`(other: U) = booleanType.createReference("${getReference()} ∈ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`∉`(other: U) = booleanType.createReference("${getReference()} ∉ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`⊆`(other: U) = booleanType.createReference("${getReference()} ⊆ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`⊂`(other: U) = booleanType.createReference("${getReference()} ⊂ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`⊄`(other: U) = booleanType.createReference("${getReference()} ⊄ ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`outside`(other: ListType<T, U>) = booleanType.createReference("${getReference()} OUTSIDE ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`intersects`(other: ListType<T, U>) = booleanType.createReference("${getReference()} INTERSECTS ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`contains`(other: U) = booleanType.createReference("${getReference()} CONTAINS ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`containsNot`(other: U) = booleanType.createReference("${getReference()} CONTAINSNOT ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`containsAll`(other: ListType<T, U>) = booleanType.createReference("${getReference()} CONTAINSALL ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`containsAny`(other: ListType<T, U>) = booleanType.createReference("${getReference()} CONTAINSANY ${other.getReference()}")

infix fun <T, U: Reference<T>>U.`containsNone`(other: ListType<T, U>) = booleanType.createReference("${getReference()} CONTAINSNONE ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`inside`(other: ListType<T, U>) = booleanType.createReference("${getReference()} INSIDE ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`notInside`(other: ListType<T, U>) = booleanType.createReference("${getReference()} NOTINSIDE ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`allInside`(other: ListType<T, U>) = booleanType.createReference("${getReference()} ALLINSIDE ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`anyInside`(other: ListType<T, U>) = booleanType.createReference("${getReference()} ANYINSIDE ${other.getReference()}")

infix fun <T, U: Reference<T>>ListType<T, U>.`noneInside`(other: ListType<T, U>) = booleanType.createReference("${getReference()} NONEINSIDE ${other.getReference()}")

