
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
        .addReferenceProperty()
        .addSuperinterface(
            RelationType::class.asClassName().parameterizedBy(
                inType.toTypeName(),
                inRecordType,
                className,
                outType.toTypeName(),
                outRecordType
            )
        )
        .addIdProperty(className, generatedClassName)
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
        .addGetReferenceFunction()
        .addCreateReferenceFunction(relationTypeName)
        .addFields(element.getAllProperties(), resolver, logger)
        .build()
}
