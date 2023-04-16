package uk.gibby.dsl.scopes

import uk.gibby.dsl.types.Reference

class TransactionScope {
    private var generated: String = "BEGIN TRANSACTION;"
    operator fun Reference<*>.unaryPlus(){
        generated += getReference()
        generated += ";"
    }
    fun getQueryText() = generated + "COMMIT TRANSACTION;"
}