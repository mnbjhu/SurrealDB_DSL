package uk.gibby.dsl.types

import kotlinx.datetime.Instant
import uk.gibby.dsl.functions.SurrealComparable
import kotlin.time.Duration

@JvmInline
value class DateTimeType(private val reference: String): Reference<Instant>, SurrealComparable<Instant>{
    override fun getReference(): String = reference
    override fun createReference(ref: String) = DateTimeType(ref)
}

val dateTimeType = DateTimeType("_")


@JvmInline
value class DurationType(private val reference: String): Reference<Duration> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = DurationType(ref)
}

val durationType = DurationType("_")
