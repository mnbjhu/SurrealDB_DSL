package types

import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.toTypeName
import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.RecordLink

class RecordFieldType(private val innerType: KSType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return RecordLink::class
            .asTypeName()
            .parameterizedBy(innerType.toTypeName(),
                ClassName(innerType.declaration.packageName.asString(), innerType.toTypeName().toString() + "Record")
            )
    }

    override fun getKotlinType(): TypeName {
        return Linked::class.asTypeName().parameterizedBy(innerType.toTypeName())
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add(CodeBlock.of("linked("))
            .add("%M)", MemberName(
                innerType.declaration.packageName.asString(),
                innerType.toTypeName().toString().replaceFirstChar(Char::lowercaseChar)
            )
            )
            .build()
    }

}