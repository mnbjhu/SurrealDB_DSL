package uk.gibby.dsl.scopes

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.core.Table
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.*
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
    inline fun <reified T, U: RecordType<T>> Table<T, U>.createContent(content: T): ListType<T, U> =
        ListType(recordType, "CREATE $name CONTENT ${surrealJson.encodeToString(content)}")
    inline fun <reified T, U: RecordType<T>> TableId<T, U>.createContent(content: T): U{
        return inner.createReference("RETURN CREATE ${getReference()} CONTENT ${surrealJson.encodeToString(content)}") as U
    }
    inline fun <reified T, U: RecordType<T>, a, A: Reference<a>> TableId<T, U>.createContent(content: T, toReturn: U.() -> A): A {
        val returned = toReturn(inner)
        return returned.createReference("RETURN CREATE ${getReference()} CONTENT ${surrealJson.encodeToString(content)} RETURN VALUE ${returned.getReference()}") as A
    }


    fun <T, U: RecordType<T>> Table<T, U>.create(scope: context(SetScope) U.() -> Unit): U {
        val setScope = SetScope()
        scope(setScope, recordType)
        return recordType.createReference("RETURN CREATE $name ${setScope.getSetString()}") as U
    }
    fun <T, U: RecordType<T>> TableId<T, U>.create(scope: context(SetScope) U.() -> Unit): U {
        val setScope = SetScope()
        scope(setScope, inner)
        return inner.createReference("RETURN CREATE ${getReference()} ${setScope.getSetString()} RETURN AFTER") as U
    }
    inline fun <reified T, U: RecordType<T>>Table<T, U>.insert(vararg items: T): ListType<T, U>{
        return insert(items.toList())
    }
    inline fun <reified T, U: RecordType<T>>Table<T, U>.insert(items: List<T>): ListType<T, U>{
        return ListType(recordType, "INSERT INTO $name ${surrealJson.encodeToString(items)}")
    }
}

