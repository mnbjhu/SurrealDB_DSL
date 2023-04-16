package uk.gibby.dsl.types

@JvmInline
value class NullableType<T, U: Reference<T>>(private val reference: String): Reference<T?> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = NullableType<T, U>(ref)
}