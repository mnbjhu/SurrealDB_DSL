package uk.gibby.dsl.types

import uk.gibby.dsl.functions.SurrealComparable

@JvmInline
value class LongType(private val reference: String): Reference<Long>, SurrealComparable<Long> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = LongType(ref)
}
val longType = LongType("_")
