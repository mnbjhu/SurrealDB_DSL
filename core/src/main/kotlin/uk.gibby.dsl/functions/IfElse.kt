package uk.gibby.dsl.functions

import uk.gibby.dsl.scopes.CodeBlockScope
import uk.gibby.dsl.types.BooleanType
import uk.gibby.dsl.types.Reference


fun <T, U: Reference<T>>`if`(condition: BooleanType, then: context(CodeBlockScope)  () -> U): ConditionalBuilder<T, U> {
    val codeBlock = CodeBlockScope()
    val result = then(codeBlock)
    with(codeBlock) { +result }
    return ConditionalBuilder("IF ${condition.getReference()} THEN ${codeBlock.getBlockText()}", result)
}

fun <T, U: Reference<T>>ConditionalBuilder<T, U>.`else`(then: context(CodeBlockScope) () -> U): U {
    val codeBlock = CodeBlockScope()
    val result = then(codeBlock)
    with(codeBlock) { +result }
    return inner.createReference(getReference() + " ELSE ${codeBlock.getBlockText()} END") as U
}


fun <T, U: Reference<T>>ConditionalBuilder<T, U>.end(): U {
    return inner.createReference(getReference() + " END") as U
}

fun <T, U: Reference<T>>ConditionalBuilder<T, U>.`else if`(condition: BooleanType, then: context(CodeBlockScope) () -> U): ConditionalBuilder<T, U> {
    val codeBlock = CodeBlockScope()
    val result = then(codeBlock)
    with(codeBlock) { +result }
    return createReference(getReference() + " ELSE IF ${condition.getReference()} THEN ${codeBlock.getBlockText()}")
}

data class ConditionalBuilder<T, U: Reference<T>>(val ref: String, internal val inner: U): Reference<T>{
    override fun getReference(): String {
        return ref
    }

    override fun createReference(ref: String): ConditionalBuilder<T, U> {
        return ConditionalBuilder(ref, inner)
    }
}


