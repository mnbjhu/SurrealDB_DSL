package types

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName

interface SurrealFieldType {
    fun getSurrealType(): TypeName
    fun getKotlinType(): TypeName
    fun getSurrealTypeFunction(): CodeBlock
}