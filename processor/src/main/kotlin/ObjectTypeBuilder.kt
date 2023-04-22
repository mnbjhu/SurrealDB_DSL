import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import uk.gibby.dsl.types.ObjectType

fun generateObjectType(element: KSClassDeclaration, resolver: Resolver, logger: KSPLogger): TypeSpec {
    val className = element.toClassName()
    val objectTypeName = "${className.simpleName}Object"
    return TypeSpec.classBuilder(objectTypeName)
        .addModifiers(KModifier.VALUE)
        .addAnnotation(JvmInline::class)
        .addReferenceProperty()
        .addSuperinterface(ObjectType::class.asClassName().parameterizedBy(className))
        .addGetReferenceFunction()
        .addCreateReferenceFunction(objectTypeName)
        .addFields(element.getAllProperties(), resolver, logger)
        .build()
}
