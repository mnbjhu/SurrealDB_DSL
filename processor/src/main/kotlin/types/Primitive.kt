package types

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import uk.gibby.dsl.types.*
import java.time.LocalDateTime
import kotlin.time.Duration

sealed class Primitive: SurrealFieldType {
    object StringField: Primitive() {
        override fun getSurrealType(): TypeName {
            return StringType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return String::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl.types", "stringType"))
        }
    }
    object BooleanField: Primitive() {
        override fun getSurrealType(): TypeName {
            return BooleanType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return Boolean::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl.types", "booleanType"))
        }
    }
    object LongField: Primitive() {
        override fun getSurrealType(): TypeName {
            return LongType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return Long::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl.types", "longType"))
        }
    }
    object DoubleField: Primitive() {
        override fun getSurrealType(): TypeName {
            return DoubleType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return Double::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl.types", "doubleType"))
        }
    }
    object DurationField: Primitive() {
        override fun getSurrealType(): TypeName {
            return DurationType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return Duration::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl.types", "durationType"))
        }
    }
    object DateTimeField: Primitive() {
        override fun getSurrealType(): TypeName {
            return DateTimeType::class.asTypeName()
        }
        override fun getKotlinType(): TypeName {
            return LocalDateTime::class.asTypeName()
        }
        override fun getSurrealTypeFunction(): CodeBlock {
            return CodeBlock.of("%M", MemberName("uk.gibby.dsl.types", "dateTimeType"))
        }
    }
}