package uk.gibby.dsl.core

import io.ktor.util.reflect.*
import uk.gibby.dsl.scopes.CodeBlockScope
import uk.gibby.dsl.types.*
import kotlin.time.Duration

abstract class Schema {
    abstract val tables: List<TableDefinition>
    abstract val scopes: List<Scope<*, *, *, *, *, *>>
    val definition =
    fun getDefinitionQuery(): String {
        var definition = ""
        scopes.forEach {
            definition += it.getDefinition()
        }
        return definition
    }

}

fun <T, U: RecordType<T>>Table<T, U>.getDefinition(): TableDefinition {

    TableDefinition(
        name,
        recordType::class.members.filter { it.returnType.instanceOf(Reference::class) }
            .associate { it.name to FieldDefinition() }
    )
}

fun getFieldType(reference: Reference<*>): Map<String, FieldDefinition> {
    when(reference) {

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
    enum class Type(val text: String) {
        BOOLEAN("bool"), STRING("string"), LONG("int"), DOUBLE("decimal"),
        DATETIME("datetime"), DURATION("duration"), ARRAY("array"), OBJECT("object")
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
