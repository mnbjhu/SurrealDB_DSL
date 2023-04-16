package uk.gibby.dsl.model.rows

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class Row1(val col1: JsonElement)