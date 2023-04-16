package uk.gibby.dsl.functions

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.booleanType
import uk.gibby.dsl.driver.surrealJson


infix fun <T> SurrealComparable<T>.lessThan(other: SurrealComparable<T>) = booleanType.createReference("(${getReference()} < ${other.getReference()})")
infix fun <T> SurrealComparable<T>.greaterThan(other: SurrealComparable<T>) = booleanType.createReference("(${getReference()} > ${other.getReference()})")
infix fun <T> SurrealComparable<T>.lessThanOrEqualTo(other: SurrealComparable<T>) = booleanType.createReference("(${getReference()} <= ${other.getReference()})")
infix fun <T> SurrealComparable<T>.greaterThanOrEqualTo(other: SurrealComparable<T>) = booleanType.createReference("(${getReference()} >= ${other.getReference()})")

inline infix fun <reified T> SurrealComparable<T>.lessThan(other: T) = booleanType.createReference("(${getReference()} < ${surrealJson.encodeToString(other)})")
inline infix fun <reified T> SurrealComparable<T>.greaterThan(other: T) = booleanType.createReference("(${getReference()} > ${surrealJson.encodeToString(other)})")
inline infix fun <reified T> SurrealComparable<T>.lessThanOrEqualTo(other: T) = booleanType.createReference("(${getReference()} <= ${surrealJson.encodeToString(other)})")
inline infix fun <reified T> SurrealComparable<T>.greaterThanOrEqualTo(other: T) = booleanType.createReference("(${getReference()} >= ${surrealJson.encodeToString(other)})")

interface SurrealComparable<T>: Reference<T>