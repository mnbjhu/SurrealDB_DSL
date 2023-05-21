package uk.gibby.dsl.model.rows

import kotlinx.serialization.Serializable
import uk.gibby.dsl.serialization.row.Row3Serializer

@Serializable(with = Row3Serializer::class)
data class Row3<T, U, V>(val col1: T, val col2: U, val col3: V)
