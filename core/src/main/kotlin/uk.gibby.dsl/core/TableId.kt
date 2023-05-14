package uk.gibby.dsl.core

import uk.gibby.dsl.scopes.*
import uk.gibby.dsl.types.*

class TableId<T, U: RecordType<T>>(reference: String, inner: U): RecordLink<T, U>(reference, inner) {

    override fun createReference(ref: String): TableId<T, U> {
        return TableId(ref, inner)
    }
    context(TransactionScope)
    fun delete(): ListType<String?, NullableType<String, StringType>> {
        return ListType(NullableType("_", stringType), "DELETE FROM ${getReference()}")
    }
    context(TransactionScope)
    fun <a, A: Reference<a>>delete(deleteScope: context(FilterScope, ReturningScope<T, U>) U.() -> A): ListType<String?, NullableType<String, StringType>> {
        val filter = FilterScopeImpl(inner)
        val returned = filter.deleteScope(ReturningScopeImpl(inner), inner)
        return ListType(
            NullableType("_", stringType),
            "DELETE FROM ${getReference()}${filter.getFilterString()} RETURN ${returned.getReference()}"
        )
    }

    context(TransactionScope)
    fun selectAll(selectScope: context(FilterScope) U.() -> Unit = {}): U {
        val filter = FilterScopeImpl(inner)
        with(filter) { selectScope(inner) }
        return inner.createReference("(SELECT * FROM ${getReference()}${filter.getFilterString()})") as U
    }

    context(TransactionScope)
    fun <r, R: Reference<r>>select(projection: context(FilterScope) U.() -> R): R {
        val filter = FilterScopeImpl(inner)
        val toSelect = with(filter) {
            projection(inner)
        }
        return toSelect.createReference("(SELECT VALUE ${toSelect.getReference()} FROM ${getReference()}${filter.getFilterString()})") as R
    }

    context(TransactionScope)
    fun <a, A: Reference<a>>update(updateScope: context(SetScope, FilterScope, ReturningScope<T, U>) U.() -> A): A {
        val setScope = SetScope()
        val filterScope = FilterScopeImpl(inner)
        val returned = updateScope(setScope, filterScope, ReturningScopeImpl(inner), inner)
        return returned.createReference("UPDATE ${getReference()}{setScope.getSetString()} ${filterScope.getFilterString()} RETURN ${returned.getReference()}") as A
    }
}