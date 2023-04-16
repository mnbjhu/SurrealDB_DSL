package uk.gibby.dsl.scopes

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.Reference

open class SetScope {
    private var text = "SET "
    fun _addParam(paramText: String){
        text += paramText
    }
    infix fun <T, U: Reference<T>>U.setAs(value: U){
        text += "${getReference()} = (${value.getReference()}),"
    }

    inline infix fun <reified T> Reference<T>.setAs(value: T) {
        _addParam("${getReference()} = ${surrealJson.encodeToString(value)},")
    }
    fun getSetString() = text.dropLast(1)
}