package uk.gibby.dsl.functions

import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.longType

fun count(value: Reference<*>) = longType.createReference("count(${value.getReference()})")
