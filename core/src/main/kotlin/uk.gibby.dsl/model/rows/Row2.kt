package uk.gibby.dsl.model.rows

import kotlinx.serialization.Serializable
import uk.gibby.dsl.serialization.row.Row2Serializer

@Serializable(with = Row2Serializer::class)
data class Row2<A, B>(val col1: A, val col2: B)