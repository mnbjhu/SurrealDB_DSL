package uk.gibby.dsl.types.row

import uk.gibby.dsl.model.rows.Row2
import uk.gibby.dsl.types.Reference

data class RowType2<a, A: Reference<a>, b, B: Reference<b>>(private val reference: String, private val col1Type: A, private val col2Type: B): Reference<Row2<a, b>>,
    RowType {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = RowType2(ref, col1Type, col2Type)

    val col1: A get() = col1Type.createReference("$reference.col1") as A
    val col2: B get() = col2Type.createReference("$reference.col2") as B
}

fun <a, A: Reference<a>, b, B: Reference<b>>rowOf(col1: A, col2: B) =  RowType2("${col1.getReference()} AS col1, ${col2.getReference()} AS col2", col1, col2)
