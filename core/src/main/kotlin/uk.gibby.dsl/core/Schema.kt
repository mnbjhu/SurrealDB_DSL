package uk.gibby.dsl.core

import uk.gibby.dsl.scopes.CodeBlockScope
import uk.gibby.dsl.types.*
import uk.gibby.dsl.types.BooleanType.Companion.FALSE
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeProjection
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf
import kotlin.time.Duration

abstract class Schema(tables: List<Table<*, *>>) {

    constructor(vararg tables: Table<*, *>): this(tables.toList())
    fun init(){
        if(!isInitialized){
            with(SchemaScope()){
                configure()
            }
            isInitialized = true
        }
    }

    private var isInitialized = false
    val tables: List<TableDefinition> = tables.map { it.getDefinition() }
    open val scopes: List<Scope<*, *, *, *, *, *>> = listOf()
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

        fun <T, U: RecordType<T>>Table<T, U>.configureFields(
            configuration: context(TableDefinitionScope) U.() -> Unit
        ) {
            configuration(TableDefinitionScope(this), recordType)
        }
        fun <T, U: RecordType<T>, c, C: RecordType<c>>Table<T, U>.permissions(`for`: Scope<*, *, *, *, c, C>, vararg types: PermissionType, `when`: U.(C) -> BooleanType): Table<T, U>{
            val definition = tables.first { it.name == name }
            types.forEach {
                val current = definition.permissions.getOrPut(it) { "" }
                val token = recordType.createReference("\$auth") as C
                definition.permissions[it] = current + "IF (\$scope == \"${`for`.name}\") THEN ${recordType.`when`(token).getReference()} ELSE "
            }
            return this
        }

        fun <T, U: RecordType<T>>Table<T, U>.noPermissionsFor(`for`: Scope<*, *, *, *, *, *>, vararg types: PermissionType): Table<T, U> {
            val definition = tables.first { it.name == name }
            types.forEach {
                val current = definition.permissions.getOrPut(it) { "" }
                definition.permissions[it] = current + "IF (\$scope == \"${`for`.name}\") THEN FALSE ELSE "
            }
            return this
        }

        fun <T, U: RecordType<T>>Table<T, U>.fullPermissionsFor(`for`: Scope<*, *, *, *, *, *>, vararg types: PermissionType): Table<T, U> {
            val definition = tables.first { it.name == name }
            types.forEach {
                val current = definition.permissions.getOrPut(it) { "" }
                definition.permissions[it] = current + "IF (\$scope == \"${`for`.name}\") THEN TRUE ELSE "
            }
            return this
        }
/*
        fun <T, U: RecordType<T>>Table<T, U>.assert(){
            val definition = tables.first { it.name == name }
            definition
        }
 */
    }

    inner class TableDefinitionScope(private val definition: TableDefinition) {
        constructor(table: Table<*, *>): this(tables.first { it.name == table.name })
        fun <T, U: Reference<T>>U.assert(condition: (U) -> BooleanType): U {
            val assertion = condition(createReference("\$value") as U).getReference()
            definition.fields[getReference()]!!.assertions.add(assertion)
            return this
        }

        fun defineIndex(name: String, vararg fields: Reference<*>) {
            definition.indexes.add("DEFINE INDEX $name ON ${definition.name} FIELDS ${fields.joinToString { it.getReference() }};")
        }
        fun defineUniqueIndex(name: String, vararg fields: Reference<*>) {
            definition.indexes.add("DEFINE INDEX $name ON ${definition.name} FIELDS ${fields.joinToString { it.getReference() }} UNIQUE;")
        }

        fun <T, U: Reference<T>, c, C: RecordType<c>>U.permissions(`for`: Scope<*, *, *, *, c, C>, vararg types: PermissionType, `when`: (C) -> BooleanType){
            val fieldDefinition = definition.fields[getReference()]!!
            types.forEach {
                val current = fieldDefinition.permissions.getOrPut(it) {
                    ""
                }
                val token = `for`.tokenTable.recordType.createReference("\$auth") as C
                fieldDefinition.permissions[it] = current + "IF (\$scope == \"${`for`.name}\") THEN ${`when`(token).getReference()} ELSE "
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
fun Table<*, *>.getDefinition(): TableDefinition {
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
    val fields: MutableMap<String, FieldDefinition>,
    val permissions: MutableMap<PermissionType, String> = mutableMapOf(),
    val indexes: MutableList<String> = mutableListOf()
) {
    fun getDefinition(): String {
        return "DEFINE TABLE $name SCHEMAFULL" +
                ( if(permissions.isNotEmpty())
                    "\nPERMISSIONS \n${permissions.entries.joinToString("\n"){ "FOR ${it.key.text} WHERE ${it.value}FALSE END" }}" else "" ) +
                ";\n" +
                fields.entries.joinToString("\n"){ it.value.getDefinition(it.key, name) } + "\n" +
                indexes.joinToString("\n")
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
                    "\nPERMISSIONS \n${permissions.entries.joinToString("\n"){ "FOR ${it.key.text} WHERE ${it.value}FALSE END" }}" else "" ) +
                ";"

    }
}

fun <a, A: Reference<a>, b, B: Reference<b>, c, C: RecordType<c>>scopeOf(
    name: String,
    sessionDuration: Duration,
    signupType: A,
    signInType: B,
    tokenTable: Table<c, C>,
    signUp: (A) -> ListType<c, C>,
    signIn: (B) -> ListType<c, C>,
) = object: Scope<a, A, b, B, c, C>(
    name,
    sessionDuration,
    signupType,
    signInType,
    tokenTable,

) {
    override fun signUp(auth: A): ListType<c, C> {
        return signUp(auth)
    }

    override fun signIn(auth: B): ListType<c, C> {
        return signIn(auth)
    }

}

abstract class Scope<a, A: Reference<a>, b, B: Reference<b>, c, C: RecordType<c>>(
    val name: String,
    private val sessionDuration: Duration,
    private val signupType: A,
    private val signInType: B,
    internal val tokenTable: Table<c, C>
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
