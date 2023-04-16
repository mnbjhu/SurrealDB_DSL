import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import uk.gibby.dsl.*
import kotlin.reflect.KClass


fun generateRecordTypeClass(element: KSClassDeclaration, resolver: Resolver, logger: KSPLogger): TypeSpec {
    val className = element.toClassName()
    val recordTypeName = "${className.simpleName}Record"
    val generatedClassName = ClassName.bestGuess("$recordTypeName")
    return TypeSpec.classBuilder(recordTypeName)
        .addModifiers(KModifier.VALUE)
        .addAnnotation(JvmInline::class)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("reference", String::class)
                .build()
        )
        .addSuperinterface(
            RecordType::class.asClassName().parameterizedBy(className)
        )
        .addProperty(
            PropertySpec.builder("reference", String::class)
                .addModifiers(KModifier.PRIVATE)
                .initializer("reference")
                .build()
        )
        .addProperty(
            PropertySpec.builder(
                "id",
                RecordLink::class.asClassName().parameterizedBy(className, generatedClassName)
            )
                .getter(
                    FunSpec.getterBuilder()
                        .addCode("return id()")
                        .build()
                )
                .addModifiers(KModifier.OVERRIDE)
                .build()
        )

        .addFunction(
            FunSpec.builder("getReference")
                .returns(String::class)
                .addModifiers(KModifier.OVERRIDE)
                .addCode("return reference")
                .build()
        )
        .addFunction(
            FunSpec.builder("createReference")
                .returns(ClassName.bestGuess(recordTypeName))
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("ref", String::class)
                .addCode("return $recordTypeName(ref)")
                .build()
        )
            .apply {
                element.getAllProperties()
                    .forEach { field ->
                        val fieldName = field.simpleName.asString()
                        val fieldType = getFieldType(field.type.resolve(), resolver, logger)
                        addProperty(
                            PropertySpec.builder(fieldName, fieldType.getSurrealType())
                                .getter(
                                    FunSpec.getterBuilder()
                                        .addCode("return attrOf(\"$fieldName\", ")
                                        .addCode(fieldType.getSurrealTypeFunction())
                                        .addCode(")")
                                        .build()
                                )
                                .build()
                        )
                    }
                }

        .build()
}

val fieldTypeMapping: Map<String, SurrealFieldType> = mapOf(
    "String" to Primitive.StringField,
    "Boolean" to Primitive.BooleanField,
)

fun getFieldType(fieldType: KSType, resolver: Resolver, logger: KSPLogger): SurrealFieldType {
    val nullableRegex = "(.+)\\?".toRegex()
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
inline fun <reified T> KSType.isAssignableFrom(resolver: Resolver): Boolean {
    val classDeclaration = requireNotNull(resolver.getClassDeclarationByName<T>()) {
        "Unable to resolve ${KSClassDeclaration::class.simpleName} for type ${T::class.simpleName}"
    }
    return isAssignableFrom(classDeclaration.asStarProjectedType())
}

fun  KSType.isAssignableFrom(clazz: KClass<*>, resolver: Resolver): Boolean {
    val classDeclaration = requireNotNull(resolver.getClassDeclarationByName(clazz.qualifiedName ?: "Unable to resolve ${KSClassDeclaration::class.simpleName} for type ${clazz.simpleName}"))
    return isAssignableFrom(classDeclaration.asStarProjectedType())
}
interface SurrealFieldType {
    fun getSurrealType(): TypeName
    fun getKotlinType(): TypeName
    fun getSurrealTypeFunction(): CodeBlock
}


sealed class Primitive(): SurrealFieldType {
    object StringField: Primitive() {
        override fun getSurrealType(): TypeName {
            return StringType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return String::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl", "stringType"))
        }
    }
    object BooleanField: Primitive() {
        override fun getSurrealType(): TypeName {
            return BooleanType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return Boolean::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl", "booleanType"))
        }
    }
}

class ListField(private val inner: SurrealFieldType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return ListType::class.asTypeName().parameterizedBy(inner.getKotlinType(), inner.getSurrealType())
    }

    override fun getKotlinType(): TypeName {
        return List::class.asTypeName().parameterizedBy(inner.getKotlinType())
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add(CodeBlock.of("%M(", MemberName("uk.gibby.dsl", "list")))
            .add(inner.getSurrealTypeFunction())
            .add(")")
            .build()
    }

}
class NullableField(private val inner: SurrealFieldType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return NullableType::class
            .asTypeName()
            .parameterizedBy(inner.getKotlinType(), inner.getSurrealType())
    }

    override fun getKotlinType(): TypeName {
        return inner.getKotlinType().copy(nullable = true)
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add(CodeBlock.of("%M(", MemberName("uk.gibby.dsl", "nullable")))
            .add(inner.getSurrealTypeFunction())
            .add(")")
            .build()
    }

}


class RecordFieldType(private val innerType: KSType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return RecordLink::class
            .asTypeName()
            .parameterizedBy(innerType.toTypeName(), ClassName(innerType.declaration.packageName.asString(), innerType.toTypeName().toString() + "Record"))
    }

    override fun getKotlinType(): TypeName {
        return Linked::class.asTypeName().parameterizedBy(innerType.toTypeName())
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add(CodeBlock.of("linked("))
            .add("%M)", MemberName(
                innerType.declaration.packageName.asString(),
                innerType.toTypeName().toString() + "Table")
            )
            .build()
    }

}

class ObjectFieldType(private val innerType: KSType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return ClassName(innerType.declaration.packageName.asString(), innerType.toTypeName().toString() + "Object")
    }

    override fun getKotlinType(): TypeName {
        return innerType.toTypeName()
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add("%M", MemberName(
                innerType.declaration.packageName.asString(),
                innerType.toTypeName().toString() + "Type")
            )
            .build()
    }

}
