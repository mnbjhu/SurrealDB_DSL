package uk.gibby.dsl.types

data class ListType<T, U: Reference<T>>(internal val inner: U, private val reference: String): Reference<List<T>> {
    override fun getReference() = reference
    override fun createReference(ref: String) = ListType(inner, ref)
}