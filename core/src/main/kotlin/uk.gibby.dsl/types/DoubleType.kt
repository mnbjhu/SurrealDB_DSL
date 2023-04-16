package uk.gibby.dsl.types

@JvmInline
value class DoubleType(private val reference: String): Reference<Double> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = DoubleType(ref)
}

val doubleType = DoubleType("_")
