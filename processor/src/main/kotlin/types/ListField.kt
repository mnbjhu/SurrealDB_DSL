package types

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import uk.gibby.dsl.types.ListType

class ListField(private val inner: SurrealFieldType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return ListType::class.asTypeName().parameterizedBy(inner.getKotlinType(), inner.getSurrealType())
    }

    override fun getKotlinType(): TypeName {
        return List::class.asTypeName().parameterizedBy(inner.getKotlinType())
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add(CodeBlock.of("%M(", MemberName("uk.gibby.dsl.types", "list")))
            .add(inner.getSurrealTypeFunction())
            .add(")")
            .build()
    }

}