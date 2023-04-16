import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import uk.gibby.dsl.core.Table

class SurrealSymbolProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return SurrealSymbolProcessor(environment.codeGenerator, environment.logger)
    }
}
class SurrealSymbolProcessor(val codeGenerator: CodeGenerator, val logger: KSPLogger) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        buildObjectClasses(resolver)
        buildRecordClasses(resolver)
        return listOf()
    }

    private fun buildRecordClasses(resolver: Resolver) {
        val classes = resolver.getSymbolsWithAnnotation("uk.gibby.dsl.annotation.Record")
        classes.forEach {
            it as KSClassDeclaration
            val baseName = it.toClassName().simpleName
            val builder = FileSpec.builder(it.packageName.asString(), baseName + "Type")
            builder.addType(generateRecordTypeClass(it, resolver, logger))
            builder.addProperty(
                PropertySpec.builder(
                    it.toClassName().simpleName + "Table",
                    Table::class.asTypeName().parameterizedBy(
                        it.toClassName(),
                        ClassName(it.packageName.asString(), baseName + "Record")
                    )
                )
                    .initializer("Table(\"$baseName\", ${baseName}Record(\"_\"))")
                    .build()
            )
            val spec = builder.build()
            spec.writeTo(codeGenerator, true)
        }
    }

    private fun buildObjectClasses(resolver: Resolver) {
        val classes = resolver.getSymbolsWithAnnotation("uk.gibby.dsl.annotation.Object")
        classes.forEach {
            it as KSClassDeclaration
            val baseName = it.toClassName().simpleName
            val builder = FileSpec.builder(it.packageName.asString(), baseName + "Object")
            builder.addType(generateObjectType(it, resolver, logger))
            builder.addProperty(
                PropertySpec.builder(
                    it.toClassName().simpleName + "Type",
                        ClassName(it.packageName.asString(), baseName + "Object")
                )
                    .initializer("${baseName}Object(\"_\")")
                    .build()
            )
            val spec = builder.build()
            spec.writeTo(codeGenerator, true)
        }
    }
}

