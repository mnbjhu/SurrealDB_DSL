package uk.gibby.dsl.types

@JvmInline
value class BooleanType(private val reference: String): Reference<Boolean> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = BooleanType(ref)
}

val booleanType = BooleanType("_")
