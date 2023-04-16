package uk.gibby.dsl.core

import uk.gibby.dsl.scopes.FilterScope
import uk.gibby.dsl.scopes.FilterScopeImpl
import uk.gibby.dsl.scopes.SetScope
import uk.gibby.dsl.types.*

data class Table<T, U: RecordType<T>>(val name: String, val recordType: U) {
    fun delete(deleteScope: context(FilterScope) U.() -> Unit = {}): NullableType<String, StringType> {
        val filter = FilterScopeImpl()
        filter.deleteScope(recordType)
        return NullableType("DELETE FROM $name ${filter.getFilterString()}")
    }

    fun selectAll(selectScope: context(FilterScope) U.() -> Unit = {}): ListType<T, U> {
        val filter = FilterScopeImpl()
        with(filter) { selectScope(recordType) }
        return ListType(recordType, "SELECT * FROM $name ${filter.getFilterString()}")
    }

    fun <r, R: Reference<r>>select(projection: context(FilterScope) U.() -> R): ListType<r, R> {
        val filter = FilterScopeImpl()
        val toSelect = with(filter) {
            projection(recordType)
        }
        return ListType(
            toSelect,
            "SELECT VALUE ${toSelect.getReference()} AS col1 FROM $name ${filter.getFilterString()}"
        )
    }

    fun update(updateScope: context(SetScope, FilterScope) U.() -> Unit): ListType<T, U> {
        val setScope = SetScope()
        val filterScope = FilterScopeImpl()
                updateScope(setScope, filterScope, recordType)

        return ListType(recordType, "UPDATE $name ${setScope.getSetString()} ${filterScope.getFilterString()}")
    }
}