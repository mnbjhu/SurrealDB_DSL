package uk.gibby.dsl.types

@JvmInline
value class LongType(private val reference: String): Reference<Long> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = LongType(ref)
}
val longType = LongType("_")
