import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo
import uk.gibby.dsl.annotation.Relation
import uk.gibby.dsl.core.Table
import uk.gibby.dsl.types.Reference

class SurrealSymbolProcessorProvider: SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return SurrealSymbolProcessor(environment.codeGenerator, environment.logger)
    }
}

class SurrealSymbolProcessor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getNewFiles().forEach { file ->
            val tableDeclarations = file.declarations.filter { declaration ->
                declaration.annotations.any {
                    it.annotationType
                        .resolve()
                        .starProjection()
                        .isAssignableFrom<uk.gibby.dsl.annotation.Table>(resolver)
                }
            }.map { it as KSClassDeclaration }
            buildRecordClasses(codeGenerator, resolver, logger, tableDeclarations)
            val objectDeclarations = file.declarations.filter { declaration ->
                declaration.annotations.any {
                    it.annotationType
                        .resolve()
                        .starProjection()
                        .isAssignableFrom<uk.gibby.dsl.annotation.Object>(resolver)
                }
            }.map { it as KSClassDeclaration }
            buildObjectClasses(codeGenerator, resolver, logger, objectDeclarations)
            val relationDeclarations = file.declarations.filter { declaration ->
                declaration.annotations.any {
                    it.annotationType
                        .resolve()
                        .starProjection()
                        .isAssignableFrom<Relation<*, *>>(resolver)
                }
            }.map { it as KSClassDeclaration }
            buildRelationClasses(codeGenerator, resolver, logger, relationDeclarations)
        }
        return listOf()
    }
}



fun buildRelationClasses(codeGenerator: CodeGenerator, resolver: Resolver, logger: KSPLogger, classes: Sequence<KSClassDeclaration>) {
    classes.forEach {
        val annotation = it.annotations.first { it.annotationType.resolve().starProjection().isAssignableFrom<Relation<*, *>>(resolver) }
        val (inType, outType) = annotation.annotationType.resolve().arguments
        val baseName = it.toClassName().simpleName
        val builder = FileSpec.builder(it.packageName.asString(), baseName + "Relation")
        builder.addType(generateRelationType(it, resolver, logger, inType.type!!.resolve(), outType.type!!.resolve()))
        builder.addProperty(
            PropertySpec.builder(
                baseName.replaceFirstChar(Char::lowercaseChar),
                Table::class.asTypeName().parameterizedBy(
                    it.toClassName(),
                    ClassName(it.packageName.asString(), baseName + "Relation")
                )
            )
                .initializer("Table(\"$baseName\", ${baseName}Relation(\"_\"))")
                .build()
        )
        val spec = builder.build()
        spec.writeTo(codeGenerator, true)
    }
}
fun buildRecordClasses(codeGenerator: CodeGenerator, resolver: Resolver, logger: KSPLogger, classes: Sequence<KSClassDeclaration>) {
    classes.forEach {
        val baseName = it.toClassName().simpleName
        val builder = FileSpec.builder(it.packageName.asString(), baseName + "Type")
        builder.addType(generateRecordTypeClass(it, resolver, logger))
        builder.addProperty(
            PropertySpec.builder(
                it.toClassName().simpleName.replaceFirstChar(Char::lowercaseChar),
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
private fun buildObjectClasses(codeGenerator: CodeGenerator, resolver: Resolver, logger: KSPLogger, classes: Sequence<KSClassDeclaration>) {
    classes.forEach {
        val baseName = it.toClassName().simpleName
        val builder = FileSpec.builder(it.packageName.asString(), baseName + "Object")
        if(Modifier.ENUM in it.modifiers) {
            val enumSpec = TypeSpec
                .classBuilder(baseName + "Object")
                .addAnnotation(JvmInline::class)
                .addModifiers(KModifier.VALUE)
                .primaryConstructor(
                    FunSpec
                        .constructorBuilder()
                        .addParameter("reference", String::class)
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("reference", String::class)
                        .addModifiers(KModifier.PRIVATE)
                        .initializer("reference")
                        .build()
                )
                .addSuperinterface(Reference::class.asTypeName().parameterizedBy(it.toClassName()))
                .addFunction(
                    FunSpec.builder("getReference")
                        .addModifiers(KModifier.OVERRIDE)
                        .returns(String::class)
                        .addCode("return reference")
                        .build()
                )
                .addFunction(
                    FunSpec.builder("createReference")
                        .addModifiers(KModifier.OVERRIDE)
                        .addParameter("ref", String::class)
                        .returns(ClassName(it.packageName.asString(), baseName + "Object"))
                        .addCode("return ${baseName}Object(ref)")
                        .build()
                )
                .build()
            builder.addType(enumSpec)
        }
        else builder.addType(generateObjectType(it, resolver, logger))
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
