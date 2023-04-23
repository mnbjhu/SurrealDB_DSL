package uk.gibby.dsl.types

data class NullableType<T, U: Reference<T>>(private val reference: String, val inner: U): Reference<T?> {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = NullableType(ref, inner)
}