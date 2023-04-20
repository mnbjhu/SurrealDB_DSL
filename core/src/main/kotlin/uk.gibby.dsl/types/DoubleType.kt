package uk.gibby.dsl.types

import uk.gibby.dsl.functions.SurrealComparable

@JvmInline
value class DoubleType(private val reference: String): Reference<Double>, SurrealComparable<Double> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = DoubleType(ref)
}

val doubleType = DoubleType("_")
