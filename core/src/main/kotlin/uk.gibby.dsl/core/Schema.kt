package uk.gibby.dsl.core

import io.ktor.util.reflect.*
import uk.gibby.dsl.scopes.CodeBlockScope
import uk.gibby.dsl.types.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.time.Duration

abstract class Schema {
    abstract val tables: List<TableDefinition>
    abstract val scopes: List<Scope<*, *, *, *, *, *>>
    fun getDefinitionQuery(): String {
        var definition = ""
        tables.forEach {
            definition += it.getDefinition()
        }
        scopes.forEach {
            definition += it.getDefinition()
        }
        return definition
    }
}

fun getTableDefinition(name: String, type: KType, reference: Reference<*>): TableDefinition {
    return TableDefinition(name, getFieldDefinition(name, type, reference))
}

fun getFieldDefinition(name: String, type: KType, reference: Reference<*>): Map<String, FieldDefinition> {
    return when {
        type.instanceOf(StringType::class) -> mapOf(name to FieldDefinition(FieldDefinition.Type.STRING, mutableMapOf(), mutableListOf()))
        type.instanceOf(BooleanType::class) -> mapOf(name to FieldDefinition(FieldDefinition.Type.BOOLEAN, mutableMapOf(), mutableListOf()))
        type.instanceOf(LongType::class) -> mapOf(name to FieldDefinition(FieldDefinition.Type.LONG, mutableMapOf(), mutableListOf()))
        type.instanceOf(DoubleType::class) -> mapOf(name to FieldDefinition(FieldDefinition.Type.DOUBLE, mutableMapOf(), mutableListOf()))
        type.instanceOf(DateTimeType::class) -> mapOf(name to FieldDefinition(FieldDefinition.Type.DATETIME, mutableMapOf(), mutableListOf()))
        type.instanceOf(DurationType::class) -> mapOf(name to FieldDefinition(FieldDefinition.Type.DURATION, mutableMapOf(), mutableListOf()))
        reference is RecordLink<*, *> -> {
            mapOf(name to FieldDefinition(FieldDefinition.Type.RecordLink(type.), mutableMapOf(), mutableListOf()))
        }
        type.instanceOf(ListType::class) -> mutableMapOf(name to FieldDefinition(FieldDefinition.Type.ARRAY, mutableMapOf(), mutableListOf()))
            .apply {
                val inner = (reference as ListType<*, Reference<*>>).inner
                val innerType = inner::class.createType()
                putAll(getFieldDefinition("$name.*", innerType, inner))
            }
        type.instanceOf(ObjectType::class) -> {
            val definition = mutableMapOf<String, FieldDefinition>()
            definition[name] = FieldDefinition(FieldDefinition.Type.OBJECT, mutableMapOf(), mutableListOf())
            val clazz = type.classifier as KClass<*>
            clazz.members
                .filter { it.returnType.instanceOf(Reference::class) }
                .forEach { definition.putAll(getFieldDefinition("$name.${it.name}", it.returnType, it.call(reference) as Reference<*>)) }
            return definition.toMap()
        }
        else -> throw Exception("Illegal Type: ${type.classifier}")
    }
}

class TableDefinition(
    val name: String,
    private val fields: Map<String, FieldDefinition>,
    private val permissions: MutableMap<PermissionType, String> = mutableMapOf(),
) {
    fun getDefinition(): String {
        return "DEFINE TABLE $name SCHEMAFULL\n" +
                ( if(permissions.isNotEmpty())
                    "\nPERMISSIONS \n${permissions.entries.joinToString("\n"){ "FOR ${it.key.name}\n${it.value}" }}" else "" ) +
                fields.entries.joinToString("\n"){ it.value.getDefinition(it.key, name) }
    }
}

data class FieldDefinition(
    val type: Type,
    val permissions: MutableMap<PermissionType, String>,
    val assertions: MutableList<String>
) {
    sealed class Type(val text: String) {
        object BOOLEAN: Type("bool")
        object STRING: Type("string")
        object LONG: Type("int")
        object DOUBLE: Type("decimal")
        object DATETIME: Type("datetime")
        object DURATION: Type("duration")
        object ARRAY: Type("array")
        object OBJECT: Type("object")
        class RecordLink(tableName: String): Type("record($tableName)")
    }
    fun getDefinition(name: String, tableName: String): String {
        return "DEFINE FIELD $name ON TABLE $tableName TYPE ${type.text}" +
                ( if(assertions.isNotEmpty()) "\nASSERT ${assertions.joinToString(" AND "){ it }}" else "" ) +
                ( if(permissions.isNotEmpty())
                    "\nPERMISSIONS \n${permissions.entries.joinToString("\n"){ "FOR ${it.key.name}\n${it.value}" }}" else "" )
    }
}

abstract class Scope<a, A: Reference<a>, b, B: Reference<b>, c, C: Reference<c>>(
    val name: String,
    private val sessionDuration: Duration,
    private val signupType: A,
    private val signInType: B,
    private val tokenType: C
) {
    abstract fun CodeBlockScope.signUp(auth: A): ListType<c, C>
    abstract fun CodeBlockScope.signIn(auth: B): ListType<c, C>

    fun getDefinition(): String {
        val signupCodeBlock = CodeBlockScope()
        signupCodeBlock.signUp(signupType.createReference("\$auth") as A)
        val signInCodeBlock = CodeBlockScope()
        signInCodeBlock.signIn(signInType.createReference("\$auth") as B)
        return "DEFINE SCOPE $name\n" +
                "SESSION $sessionDuration" +
                "SIGNUP ${signupCodeBlock.getBlockText()}\n" +
                "SIGNIN ${signInCodeBlock.getBlockText()}\n"
    }
    inner class Permission(
        val type: PermissionType,
        val forScope: Scope<*, *, *, *, *, *>,
        val condition: CodeBlockScope.(C) -> BooleanType
    )
}
enum class PermissionType {
    Create, Update, Select, Delete
}
