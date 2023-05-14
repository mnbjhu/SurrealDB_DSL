package uk.gibby.dsl.types

import uk.gibby.dsl.model.Linked

open class RecordLink<T, out U: RecordType<T>>(private val reference: String, val inner: U):
    Reference<Linked<T>> {

    override fun getReference() = reference
    override fun createReference(ref: String): Reference<Linked<T>> = RecordLink(ref, inner)
}

