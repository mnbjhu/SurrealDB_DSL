package uk.gibby.dsl.core

import uk.gibby.dsl.scopes.CodeBlockScope
import uk.gibby.dsl.types.*
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.time.Duration

abstract class Schema {

    fun init(){
        if(!isInitialized){
            with(SchemaScope()){
                configure()
            }
            isInitialized = true
        }
    }

    private var isInitialized = false
    abstract val tables: List<TableDefinition>
    abstract val scopes: List<Scope<*, *, *, *, *, *>>
    fun getDefinitionQuery(): String {
        init()
        var definition = "BEGIN TRANSACTION;\n"
        tables.forEach {
            definition += "${it.getDefinition()}\n"
        }
        scopes.forEach {
            definition += "${it.getDefinition()};\n"
        }
        return "$definition\nCOMMIT TRANSACTION;"
    }
    open fun SchemaScope.configure() {}
    inner class SchemaScope {
        fun <T, U: RecordType<T>, c, C: RecordType<c>>Table<T, U>.permissions(`for`: Scope<*, *, *, *, c, C>, vararg types: PermissionType, `when`: C.() -> BooleanType){
            println(tables)
            val definition = tables.first { it.name == name }
            types.forEach {
                val current = definition.permissions.getOrPut(it) {
                    ""
                }
                val token = recordType.createReference("\$auth") as C
                definition.permissions[it] = current + "IF (\$scope == \"${`for`.name}\") THEN ${token.`when`().getReference()} ELSE "
            }
        }
    }
}

fun getTableDefinition(name: String, type: KType, reference: Reference<*>): TableDefinition {
    val definition = mutableMapOf<String, FieldDefinition>()
    val clazz = reference::class
    clazz.members
        .filter {
            it.returnType.isSubtypeOf(Reference::class.createType(listOf(KTypeProjection.STAR))) &&
                    it.parameters.size == 1 && it.name != "id"
        }
        .forEach { definition.putAll(getFieldDefinition(it.name, it.returnType, it.call(reference) as Reference<*>)) }
    return TableDefinition(name, definition)
}
inline fun <reified T, U: RecordType<T>>Table<T, U>.getDefinition(): TableDefinition {
    return getTableDefinition(name, recordType::class.createType(), recordType)
}

const val assertNotNull = "\$value != NONE"

fun getFieldDefinition(name: String, type: KType, reference: Reference<*>): Map<String, FieldDefinition> {
    return when(reference){
        is StringType -> mapOf(name to FieldDefinition(FieldDefinition.Type.STRING, mutableMapOf(), mutableListOf(assertNotNull)))
        is BooleanType -> mapOf(name to FieldDefinition(FieldDefinition.Type.BOOLEAN, mutableMapOf(), mutableListOf(assertNotNull)))
        is LongType -> mapOf(name to FieldDefinition(FieldDefinition.Type.LONG, mutableMapOf(), mutableListOf(assertNotNull)))
        is DoubleType -> mapOf(name to FieldDefinition(FieldDefinition.Type.DOUBLE, mutableMapOf(), mutableListOf(assertNotNull)))
        is DateTimeType -> mapOf(name to FieldDefinition(FieldDefinition.Type.DATETIME, mutableMapOf(), mutableListOf(assertNotNull)))
        is DurationType -> mapOf(name to FieldDefinition(FieldDefinition.Type.DURATION, mutableMapOf(), mutableListOf(assertNotNull)))
        is NullableType<*, *> -> {
            val starProjectedParams = reference.inner::class.typeParameters.map { KTypeProjection.STAR }
            val innerType = reference.inner::class.createType(starProjectedParams)
            val entries = getFieldDefinition(name, innerType, reference.inner).entries
            val topLevelDefinition = entries.minBy { it.key }
            val nullableTypeDef = listOf(topLevelDefinition.key to topLevelDefinition.value.copy(assertions = mutableListOf())) +
                    entries
                .filter { it.key != topLevelDefinition.key }
                .map { it.key to it.value }
            nullableTypeDef
                .associate { it.first to it.second }
        }
        is RecordLink<*, *> -> {
            mapOf(name to FieldDefinition(FieldDefinition.Type.RecordLink((reference.inner::class).simpleName.toString().removeSuffix("Record")), mutableMapOf(), mutableListOf()))
        }
        is ListType<*, *> -> mutableMapOf(name to FieldDefinition(FieldDefinition.Type.ARRAY, mutableMapOf(), mutableListOf(assertNotNull)))
            .apply {
                val starProjectedParams = reference.inner::class.typeParameters
                    .map { KTypeProjection.STAR }
                val innerType = reference.inner::class.createType(starProjectedParams)
                putAll(getFieldDefinition("$name.*", innerType, reference.inner))
            }
        is ObjectType<*> -> {
            val definition = mutableMapOf<String, FieldDefinition>()
            definition[name] = FieldDefinition(FieldDefinition.Type.OBJECT, mutableMapOf(), mutableListOf(assertNotNull))
            val clazz = type.classifier as KClass<*>
            clazz.members
                .filter {
                    it.returnType.isSubtypeOf(Reference::class.createType(listOf(KTypeProjection.STAR))) &&
                            it.parameters.size == 1
                }
                .forEach { definition.putAll(getFieldDefinition("$name.${it.name.removePrefix("`").removeSuffix("`")}", it.returnType, it.call(reference) as Reference<*>)) }
            return definition.toMap()
        }
        is EnumType -> mapOf(name to FieldDefinition(FieldDefinition.Type.STRING, mutableMapOf(), mutableListOf(assertNotNull)))
        else -> throw Exception("Illegal Type: ${type.classifier}")
    }
}

class TableDefinition(
    val name: String,
    private val fields: Map<String, FieldDefinition>,
    val permissions: MutableMap<PermissionType, String> = mutableMapOf(),
) {
    fun getDefinition(): String {
        return "DEFINE TABLE $name SCHEMAFULL" +
                ( if(permissions.isNotEmpty())
                    "\nPERMISSIONS \n${permissions.entries.joinToString("\n"){ "FOR ${it.key.text} WHERE ${it.value}FALSE END" }}" else "" ) +
                ";" +
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
                    "\nPERMISSIONS \n${permissions.entries.joinToString("\n"){ "FOR ${it.key.name}\n${it.value}" }}" else "" ) +
                ";"

    }
}

abstract class Scope<a, A: Reference<a>, b, B: Reference<b>, c, C: RecordType<c>>(
    val name: String,
    private val sessionDuration: Duration,
    private val signupType: A,
    private val signInType: B,
    private val tokenTable: Table<c, C>
) {
    abstract fun signUp(auth: A): ListType<c, C>
    abstract fun signIn(auth: B): ListType<c, C>

    fun getDefinition(): String {
        val signUpToken = signUp(signupType.createReference("\$creds") as A)

        val signInToken = signIn(signInType.createReference("\$auth") as B)

        return "DEFINE SCOPE $name\n" +
                "SESSION $sessionDuration\n" +
                "SIGNUP ( ${signUpToken.getReference()} )\n" +
                "SIGNIN ( ${signInToken.getReference()} )\n"
    }
    inner class Permission(
        val type: PermissionType,
        val forScope: Scope<*, *, *, *, *, *>,
        val condition: CodeBlockScope.(C) -> BooleanType
    )
}
enum class PermissionType(val text: String) {
    Create("create"), Update("update"), Select("select"), Delete("delete")
}
