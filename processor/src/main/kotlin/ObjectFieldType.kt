import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.toTypeName

class ObjectFieldType(private val innerType: KSType): SurrealFieldType {
    override fun getSurrealType(): TypeName {
        return ClassName(innerType.declaration.packageName.asString(), innerType.toTypeName().toString() + "Object")
    }

    override fun getKotlinType(): TypeName {
        return innerType.toTypeName()
    }

    override fun getSurrealTypeFunction(): CodeBlock {
        return CodeBlock.builder()
            .add("%M", MemberName(
                innerType.declaration.packageName.asString(),
                innerType.toTypeName().toString() + "Type"
            )
            )
            .build()
    }
}