
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.*

fun generateRelationType(element: KSClassDeclaration, resolver: Resolver, logger: KSPLogger, inType: KSType, outType: KSType, ): TypeSpec {
    val className = element.toClassName()
    val relationTypeName = "${className.simpleName}Relation"
    val generatedClassName = ClassName.bestGuess(relationTypeName)
    val inRecordType = with(inType.declaration){ ClassName(packageName.asString(), simpleName.asString() + "Record") }
    val outRecordType = with(outType.declaration){ ClassName(packageName.asString(), simpleName.asString() + "Record") }
    return TypeSpec.classBuilder(relationTypeName)
        .addModifiers(KModifier.VALUE)
        .addAnnotation(JvmInline::class)
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameter("reference", String::class)
                .build()
        )
        .addSuperinterface(
            RelationType::class.asClassName().parameterizedBy(
                inType.toTypeName(),
                inRecordType,
                className,
                outType.toTypeName(),
                outRecordType
            )
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

        .addProperty(
            PropertySpec.builder(
                "in",
                RecordLink::class.asClassName().parameterizedBy(inType.toTypeName(), inRecordType)
            )
                .getter(
                    FunSpec.getterBuilder()
                        .addCode("return linked(%M)", MemberName(
                            inType.declaration.packageName.asString(),
                            inType.toString().replaceFirstChar(Char::lowercaseChar)
                        )
                        )
                        .build()
                )
                .addModifiers(KModifier.OVERRIDE)
                .build()
        )
        .addProperty(
            PropertySpec.builder(
                "out",
                RecordLink::class.asClassName().parameterizedBy(outType.toTypeName(), outRecordType)
            )
                .getter(
                    FunSpec.getterBuilder()
                        .addCode("return linked(%M)", MemberName(
                            outType.declaration.packageName.asString(),
                            outType.toString().replaceFirstChar(Char::lowercaseChar)
                        )
                    )
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
                .returns(ClassName.bestGuess(relationTypeName))
                .addModifiers(KModifier.OVERRIDE)
                .addParameter("ref", String::class)
                .addCode("return $relationTypeName(ref)")
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
