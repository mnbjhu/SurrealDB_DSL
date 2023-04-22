import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import uk.gibby.dsl.types.NullableType

class NullableField(private val inner: SurrealFieldType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return NullableType::class
            .asTypeName()
            .parameterizedBy(inner.getKotlinType(), inner.getSurrealType())
    }

    override fun getKotlinType(): TypeName {
        return inner.getKotlinType().copy(nullable = true)
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add(CodeBlock.of("%M(", MemberName("uk.gibby.dsl.types", "nullable")))
            .add(inner.getSurrealTypeFunction())
            .add(")")
            .build()
    }

}