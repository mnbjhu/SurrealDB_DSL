package uk.gibby.dsl.scopes

import kotlinx.serialization.encodeToString
import sun.reflect.generics.tree.ReturnType
import uk.gibby.dsl.core.Table
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.ListType
import uk.gibby.dsl.types.RecordType
import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.RelationType
import kotlin.reflect.KProperty



class TransactionScope {
    private var generated: String = "BEGIN TRANSACTION;"
    operator fun Reference<*>.unaryPlus(){
        generated += getReference()
        generated += ";\n"
    }
    fun getQueryText() = generated + "COMMIT TRANSACTION;"

    inline fun <a, A: RecordType<a>, reified b, B: RelationType<a, A, b, c, C>, c, C: RecordType<c>>relate(from: ListType<a, A>, with: Table<b, B>, to: ListType<c, C>, content: b): ListType<b, B> {
        return ListType(with.recordType, "RELATE ${from.getReference()}->${with.name}->${to.getReference()} CONTENT ${surrealJson.encodeToString(content)}")
    }
    operator fun <T, U: Reference<T>>U.getValue(thisRef: Any?, property: KProperty<*>): U =
        this.createReference("\$${property.name}").also { +createReference("LET \$${property.name} = ${getReference()}") } as U
}

