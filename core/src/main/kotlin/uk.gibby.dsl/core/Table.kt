package uk.gibby.dsl.core

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.scopes.*
import uk.gibby.dsl.types.*

data class Table<T, U: RecordType<T>>(val name: String, val recordType: U) {
    context(TransactionScope)
    fun delete(): ListType<String?, NullableType<String, StringType>> {
        return ListType(NullableType("_", stringType), "DELETE FROM $name")
    }
    context(TransactionScope)
    fun <a, A: Reference<a>>delete(deleteScope: context(FilterScope, ReturningScope<T, U>) U.() -> A): ListType<String?, NullableType<String, StringType>> {
        val filter = FilterScopeImpl(recordType)
        val returned = filter.deleteScope(ReturningScopeImpl(recordType), recordType)
        return ListType(NullableType("_", stringType), "DELETE FROM $name${filter.getFilterString()} RETURN ${returned.getReference()}")
    }

    context(TransactionScope)
    fun selectAll(selectScope: context(FilterScope) U.() -> Unit = {}): ListType<T, U> {
        val filter = FilterScopeImpl(recordType)
        with(filter) { selectScope(recordType) }
        return ListType(recordType, "SELECT * FROM $name${filter.getFilterString()}")
    }

    context(TransactionScope)
    fun <r, R: Reference<r>>select(projection: context(FilterScope) U.() -> R): ListType<r, R> {
        val filter = FilterScopeImpl(recordType)
        val toSelect = with(filter) {
            projection(recordType)
        }
        return ListType(
            toSelect,
            "SELECT VALUE ${toSelect.getReference()} FROM $name${filter.getFilterString()}"
        )
    }

    context(TransactionScope)
    fun <a, A: Reference<a>>update(updateScope: context(SetScope, FilterScope, ReturningScope<T, U>) U.() -> A): ListType<a, A> {
        val setScope = SetScope()
        val filterScope = FilterScopeImpl(recordType)
        val returned = updateScope(setScope, filterScope, ReturningScopeImpl(recordType), recordType)
        return ListType(returned, "UPDATE $name ${setScope.getSetString()} ${filterScope.getFilterString()} " +
                "RETURN ${when(val ref = returned.getReference()){
                    "AFTER" -> "AFTER"
                    "BEFORE" -> "BEFORE"
                    "NONE" -> "NONE"
                    else -> "VALUE $ref"
                }}")
    }
    operator fun get(id: String): TableId<T, U> {
        return TableId("$name:$id", recordType)
    }
}

