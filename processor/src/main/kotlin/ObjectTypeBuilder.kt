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

fun generateObjectType(element: KSClassDeclaration, resolver: Resolver, logger: KSPLogger): TypeSpec {
    val className = element.toClassName()
    val recordTypeName = "${className.simpleName}Object"
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
            ObjectType::class.asClassName().parameterizedBy(className)
        )
        .addProperty(
            PropertySpec.builder("reference", String::class)
                .addModifiers(KModifier.PRIVATE)
                .initializer("reference")
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
