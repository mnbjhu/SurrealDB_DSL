package uk.gibby.dsl.scopes

import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.BooleanType
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.RecordLink
import uk.gibby.dsl.types.RecordType

class UpdateScope: SetScope(), FilterScope {
    private var condition: String? = null
    override fun getFilterString() =
        if(condition == null) "" else " WHERE $condition"

    override fun where(condition: BooleanType) {
        this.condition = condition.getReference()
    }

    override fun <T, U : RecordType<T>> fetch(items: ListType<Linked<T>, RecordLink<T, U>>) {
        TODO("Not yet implemented")
    }
}