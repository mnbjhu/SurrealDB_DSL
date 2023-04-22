import com.google.devtools.ksp.getClassDeclarationByName
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import uk.gibby.dsl.types.RecordLink
import uk.gibby.dsl.types.RecordType
import kotlin.reflect.KClass


fun generateRecordTypeClass(element: KSClassDeclaration, resolver: Resolver, logger: KSPLogger): TypeSpec {
    val className = element.toClassName()
    val recordTypeName = "${className.simpleName}Record"
    val generatedClassName = ClassName.bestGuess(recordTypeName)
    return TypeSpec.classBuilder(recordTypeName)
        .addModifiers(KModifier.VALUE)
        .addAnnotation(JvmInline::class)
        .addSuperinterface(RecordType::class.asClassName().parameterizedBy(className))
        .addReferenceProperty()
        .addIdProperty(className, generatedClassName)
        .addCreateReferenceFunction(recordTypeName)
        .addGetReferenceFunction()
        .addFields(element.getAllProperties(), resolver, logger)
        .build()
}

fun TypeSpec.Builder.addFields(elements: Sequence<KSPropertyDeclaration>, resolver: Resolver, logger: KSPLogger): TypeSpec.Builder {
    elements.forEach { field ->
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
    return this
}

fun TypeSpec.Builder.addReferenceProperty(): TypeSpec.Builder {
    primaryConstructor(
        FunSpec.constructorBuilder()
            .addParameter("reference", String::class)
            .build()
    )
    addProperty(
        PropertySpec.builder("reference", String::class)
            .addModifiers(KModifier.PRIVATE)
            .initializer("reference")
            .build()
    )
    return this
}


fun TypeSpec.Builder.addIdProperty(className: ClassName, generatedClassName: ClassName): TypeSpec.Builder {
    addProperty(
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
    return this
}


fun TypeSpec.Builder.addGetReferenceFunction(): TypeSpec.Builder {
    addFunction(
        FunSpec.builder("getReference")
            .returns(String::class)
            .addModifiers(KModifier.OVERRIDE)
            .addCode("return reference")
            .build()
    )
    return this
}
fun TypeSpec.Builder.addCreateReferenceFunction(recordTypeName: String): TypeSpec.Builder {
    addFunction(
        FunSpec.builder("createReference")
            .returns(ClassName.bestGuess(recordTypeName))
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("ref", String::class)
            .addCode("return $recordTypeName(ref)")
            .build()
    )
    return this
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


