package uk.gibby.dsl.types.row

import uk.gibby.dsl.model.rows.Row3
import uk.gibby.dsl.types.Reference

data class RowType3<a, A: Reference<a>, b, B: Reference<b>, c, C: Reference<c>>(private val reference: String, private val col1Type: A, private val col2Type: B, private val col3Type: C): Reference<Row3<a, b, c>>,
    RowType {
    override fun getReference(): String = reference
    override fun createReference(ref: String) = RowType3(ref, col1Type, col2Type, col3Type)

    val col1: A get() = col1Type.createReference("$reference.col1") as A
    val col2: B get() = col2Type.createReference("$reference.col2") as B
    val col3: C get() = col3Type.createReference("$reference.col3") as C
}

fun <a, A: Reference<a>, b, B: Reference<b>, c, C: Reference<c>>rowOf(col1: A, col2: B, col3: C) =  RowType3("${col1.getReference()} AS col1, ${col2.getReference()} AS col2, ${ col3.getReference() } AS col3", col1, col2, col3)
