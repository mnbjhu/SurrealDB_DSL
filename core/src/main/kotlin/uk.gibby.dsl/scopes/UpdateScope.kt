package uk.gibby.dsl.scopes

import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.*

typealias UnitType = NullableType<String, StringType>

class UpdateScope<T, U: RecordType<T>>: SetScope(), FilterScope {
    private var condition: String? = null

    override fun getFilterString(): String {
        return condition!!
    }

    override fun <T, U : RecordType<T>> U.where(condition: BooleanType): UnitType {
        if(this@UpdateScope.condition == null) { this@UpdateScope.condition = " WHERE ${condition.getReference()}" }
            else { this@UpdateScope.condition += " AND ${condition.getReference()}" }
        return this@UpdateScope.None
    }



    override fun <T, U : RecordType<T>> fetch(items: ListType<Linked<T>, RecordLink<T, U>>): UnitType {
        TODO("Not yet implemented")
    }


}

interface ReturningScope<T, U: RecordType<T>>{
    val type: U

    val None
        get() = nullable(stringType).createReference("NONE")
    val Before
        get() = type.createReference("BEFORE") as U

    val After
        get() = type.createReference("AFTER") as U
}

class ReturningScopeImpl<T, U: RecordType<T>>(override val type: U): ReturningScope<T, U>
