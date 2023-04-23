package types

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSType
import isAssignableFrom
import uk.gibby.dsl.annotation.Object
import uk.gibby.dsl.model.Linked

val fieldTypeMapping: Map<String, SurrealFieldType> = mapOf(
    "String" to Primitive.StringField,
    "Boolean" to Primitive.BooleanField,
    "Long" to Primitive.LongField,
    "Double" to Primitive.DoubleField,
    "Duration" to Primitive.DurationField,
    "Instant" to Primitive.DateTimeField,
)
fun getFieldType(fieldType: KSType, resolver: Resolver, logger: KSPLogger): SurrealFieldType {
    if (fieldType.isMarkedNullable) {
        val nonNullField = getFieldType(fieldType.makeNotNullable(), resolver, logger)
        return NullableField(nonNullField)
    }
    fieldType.toString()
    if (fieldType.starProjection().isAssignableFrom<List<*>>(resolver)) {
        val innerType = fieldType.arguments[0].type!!.resolve()
        val innerFieldType = getFieldType(innerType, resolver, logger)
        return ListField(innerFieldType)
    }
    if (fieldType.starProjection().isAssignableFrom<Linked<*>>(resolver)) {
        val innerType = fieldType.arguments[0].type!!.resolve()
        return RecordFieldType(innerType)
    }

    if (fieldType.declaration.annotations.any { it.annotationType.resolve().isAssignableFrom<Object>(resolver) }) {
        return ObjectFieldType(fieldType)
    }
    return fieldTypeMapping[fieldType.toString()]
        ?: throw IllegalStateException("Unsupported field type: $fieldType")
}
