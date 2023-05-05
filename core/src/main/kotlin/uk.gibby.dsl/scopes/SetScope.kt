package uk.gibby.dsl.scopes

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.nullable
import uk.gibby.dsl.types.stringType

open class SetScope {
    val None = nullable(stringType).createReference("NONE")
    private var text = "SET "
    fun _addParam(paramText: String){
        text += paramText
    }

    infix fun <T, U: Reference<T>>U.setAs(value: U): UnitType {
        text += "${getReference()} = (${value.getReference()}),"
        return None
    }
    infix fun <T, U: Reference<T>>ListType<T, U>.setAs(value: List<U>): UnitType {
        text += "${getReference()} = [${value.joinToString { it.getReference() }}],"
        return None
    }
    infix fun <T, U: Reference<T>>ListType<T, U>.`to`(value: List<U>): UnitType {
        text += "${getReference()} = [${value.joinToString { it.getReference() }}],"
        return None
    }


    inline infix fun <reified T> Reference<T>.setAs(value: T): UnitType {
        _addParam("${getReference()} = ${surrealJson.encodeToString(value)},")
        return None
    }
    inline infix fun <reified T> Reference<T>.`to`(value: T): UnitType {
        _addParam("${getReference()} = ${surrealJson.encodeToString(value)},")
        return None
    }
    fun getSetString() = text.dropLast(1)
}
