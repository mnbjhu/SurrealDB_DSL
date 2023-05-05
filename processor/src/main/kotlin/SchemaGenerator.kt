import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import uk.gibby.dsl.core.Schema
import uk.gibby.dsl.core.TableDefinition

/*
fun buildSchema(tables: Sequence<KSClassDeclaration>): FileSpec {
    val fileBuilder = FileSpec.builder("", "TypedSchema")
    val classBuilder = TypeSpec
        .classBuilder("TypedSchema")
        .superclass(Schema::class.asTypeName())
        .addModifiers(KModifier.ABSTRACT)
    val tablesList = CodeBlock
        .builder()
        .add("listOf(")
    tables.forEach {
        tablesList.add("%M.%M(),", MemberName(
            it.packageName.asString(),
            it.simpleName.asString().replaceFirstChar(Char::lowercaseChar)),
            MemberName(
                "uk.gibby.dsl.core",
                "getDefinition"
            )
        )
    }
    tablesList.add(")")
    fileBuilder.addType(
        classBuilder.addProperty(
            PropertySpec
                .builder("tables", typeNameOf<List<TableDefinition>>())
                .addModifiers(KModifier.OVERRIDE)
                .initializer(tablesList.build())
                .build()
        ).build()
    )
    return fileBuilder.build()
}

 */