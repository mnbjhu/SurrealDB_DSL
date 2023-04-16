import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import uk.gibby.dsl.Record
import kotlin.math.log
import kotlin.reflect.KClass

class SurrealSymbolProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return SurrealSymbolProcessor(environment.codeGenerator, environment.logger)
    }
}
class SurrealSymbolProcessor(val codeGenerator: CodeGenerator, val logger: KSPLogger) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val classes = resolver.getSymbolsWithAnnotation("uk.gibby.dsl.Record")
        classes.forEach {
            it as KSClassDeclaration
            val builder = FileSpec.builder(it.packageName.asString(), it.toClassName().simpleName + "Type")
            builder.addType(generateRecordTypeClass(it, resolver, logger))
            val spec = builder.build()
            spec.writeTo(codeGenerator, true)
        }
        return listOf()
    }
}

class Visitor: KSVisitorVoid(){
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {

    }
}
