package uk.gibby.dsl.scopes

import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.BooleanType
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.RecordLink
import uk.gibby.dsl.types.RecordType

class FilterScopeImpl: FilterScope {
    private var where: String? = null
    private var fetch: String? = null
    override fun getFilterString(): String {
        var r = ""
        if(where != null) r += "WHERE $where"
        if(fetch != null) r += "FETCH $fetch"
        return r

    }

    override fun where(condition: BooleanType) {
        where = condition.getReference()
    }

    override fun <T, U : RecordType<T>> fetch(items: ListType<Linked<T>, RecordLink<T, U>>) {
        fetch = items.getReference()
    }

}