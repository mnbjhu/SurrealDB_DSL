package uk.gibby.dsl.core

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.scopes.FilterScope
import uk.gibby.dsl.scopes.FilterScopeImpl
import uk.gibby.dsl.scopes.SetScope
import uk.gibby.dsl.types.*

data class Table<T, U: RecordType<T>>(val name: String, val recordType: U) {
    fun delete(deleteScope: context(FilterScope) U.() -> Unit = {}): NullableType<String, StringType> {
        val filter = FilterScopeImpl()
        filter.deleteScope(recordType)
        return NullableType("DELETE FROM $name${filter.getFilterString()}")
    }

    fun selectAll(selectScope: context(FilterScope) U.() -> Unit = {}): ListType<T, U> {
        val filter = FilterScopeImpl()
        with(filter) { selectScope(recordType) }
        return ListType(recordType, "SELECT * FROM $name${filter.getFilterString()}")
    }

    fun <r, R: Reference<r>>select(projection: context(FilterScope) U.() -> R): ListType<r, R> {
        val filter = FilterScopeImpl()
        val toSelect = with(filter) {
            projection(recordType)
        }
        return ListType(
            toSelect,
            "SELECT VALUE ${toSelect.getReference()} FROM $name${filter.getFilterString()}"
        )
        TODO()
    }

    fun update(updateScope: context(SetScope, FilterScope) U.() -> Unit): ListType<T, U> {
        val setScope = SetScope()
        val filterScope = FilterScopeImpl()
                updateScope(setScope, filterScope, recordType)

        return ListType(recordType, "UPDATE $name ${setScope.getSetString()} ${filterScope.getFilterString()}")
    }
    operator fun get(id: String): Table<T, U> {
        return Table("$name:$id", recordType)
    }
}

inline fun <reified T, U: RecordType<T>>Table<T, U>.insert(vararg items: T): ListType<T, U>{
    return ListType(recordType, "INSERT INTO $name ${surrealJson.encodeToString(items.toList())}")
}
