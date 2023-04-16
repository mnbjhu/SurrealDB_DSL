package uk.gibby.dsl.types

import uk.gibby.dsl.model.Linked

data class RecordLink<T, out U: RecordType<T>>(private val reference: String, internal val inner: U):
    Reference<Linked<T>> {

    override fun getReference() = reference
    override fun createReference(ref: String): Reference<Linked<T>> = RecordLink<T, U>(ref, inner)
}