package uk.gibby.dsl.scopes

import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.BooleanType
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.RecordLink
import uk.gibby.dsl.types.RecordType

interface FilterScope {
    fun getFilterString(): String
    fun <T, U: RecordType<T>>U.where(condition: BooleanType): UnitType
    fun <T, U: RecordType<T>>fetch(items: ListType<Linked<T>, RecordLink<T, U>>): UnitType
}