package uk.gibby.dsl.types

import uk.gibby.dsl.functions.SurrealComparable

@JvmInline
value class StringType(private val reference: String): Reference<String>, SurrealComparable<String> {
    override fun getReference(): String = reference
    override fun createReference(ref: String): Reference<String> = StringType(ref)
}
val stringType = StringType("_")
