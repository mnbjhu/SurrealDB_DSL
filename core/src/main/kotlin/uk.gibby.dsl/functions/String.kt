package uk.gibby.dsl.functions

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.*

object SurrealString {
    fun concat(first: StringType, second: StringType) = stringType.createReference("string::concat(${first.getReference()},${second.getReference()})")

    fun endsWith(first: StringType, second: StringType) = booleanType.createReference("string::endsWith(${first.getReference()},${second.getReference()})")
    fun endsWith(first: String, second: StringType) = booleanType.createReference("string::endsWith(${surrealJson.encodeToString(first)},${second.getReference()})")
    fun endsWith(first: StringType, second: String) = booleanType.createReference("string::endsWith(${first.getReference()},${surrealJson.encodeToString(second)}")

    fun startsWith(first: StringType, second: StringType) = booleanType.createReference("string::startsWith(${first.getReference()},${second.getReference()})")
    fun startsWith(first: String, second: StringType) = booleanType.createReference("string::startsWith(${surrealJson.encodeToString(first)},${second.getReference()})")
    fun startsWith(first: StringType, second: String) = booleanType.createReference("string::startsWith(${first.getReference()},${surrealJson.encodeToString(second)})")

    fun replace(text: StringType, searchText: String, replaceText: String) = stringType.createReference("string::replace(${text.getReference()},${surrealJson.encodeToString(searchText)},${surrealJson.encodeToString(String.serializer(), replaceText)})")
    fun replace(text: StringType, searchText: StringType, replaceText: String) = stringType.createReference("string::replace(${text.getReference()},${searchText.getReference()},${surrealJson.encodeToString(String.serializer(), replaceText)})")
    fun replace(text: String, searchText: StringType, replaceText: String) = stringType.createReference("string::replace(${surrealJson.encodeToString(String.serializer(), text)},${searchText.getReference()},${surrealJson.encodeToString(String.serializer(), replaceText)})")
    fun replace(text: String, searchText: StringType, replaceText: StringType) = stringType.createReference("string::replace(${surrealJson.encodeToString(String.serializer(), text)},${searchText.getReference()},${replaceText.getReference()})")
    fun replace(text: String, searchText: String, replaceText: StringType) = stringType.createReference("string::replace(${surrealJson.encodeToString(String.serializer(), text)},${surrealJson.encodeToString(String.serializer(), searchText)},${replaceText.getReference()})")
    fun replace(text: StringType, searchText: String, replaceText: StringType) = stringType.createReference("string::replace(${text.getReference()},${surrealJson.encodeToString(String.serializer(), searchText)},${replaceText.getReference()})")
    fun replace(text: StringType, searchText: StringType, replaceText: StringType) = stringType.createReference("string::replace(${text.getReference()},${searchText.getReference()},${replaceText.getReference()})")

    fun join(deliminator: StringType, items: List<StringType>) = stringType.createReference("string::join(${deliminator.getReference()},${items.joinToString(","){ it.getReference() }})")

    fun length(value: StringType) = longType.createReference("string::length(${value.getReference()})")

    fun lowercase(value: StringType) = stringType.createReference("string::lowercase(${value.getReference()})")

    fun uppercase(value: StringType) = stringType.createReference("string::uppercase(${value.getReference()})")

    fun repeat(value: StringType, count: LongType) = stringType.createReference("string::repeat(${value.getReference()},${count.getReference()})")
    fun repeat(value: String, count: LongType) = stringType.createReference("string::repeat(${surrealJson.encodeToString(String.serializer(),value)},${count.getReference()})")
    fun repeat(value: StringType, count: Long) = stringType.createReference("string::repeat(${value.getReference()},${surrealJson.encodeToString(Long.serializer(),count)})")

    fun reverse(value: StringType) = stringType.createReference("string::reverse(${value.getReference()})")

    fun slice(text: StringType, start: Long, length: Long) = stringType.createReference("string::slice(${text.getReference()},${surrealJson.encodeToString(Long.serializer(), start)},${surrealJson.encodeToString(Long.serializer(), length)})")
    fun slice(text: String, start: LongType, length: Long) = stringType.createReference("string::slice(${surrealJson.encodeToString(String.serializer(), text)},${start.getReference()},${surrealJson.encodeToString(Long.serializer(), length)})")
    fun slice(text: String, start: Long, length: LongType) = stringType.createReference("string::slice(${surrealJson.encodeToString(String.serializer(), text)},${surrealJson.encodeToString(Long.serializer(), start)},${length.getReference()})")
    fun slice(text: StringType, start: LongType, length: Long) = stringType.createReference("string::slice(${text.getReference()},${start.getReference()},${surrealJson.encodeToString(Long.serializer(), length)})")
    fun slice(text: String, start: LongType, length: LongType) = stringType.createReference("string::slice(${surrealJson.encodeToString(String.serializer(), text)},${start.getReference()},${length.getReference()})")
    fun slice(text: StringType, start: Long, length: LongType) = stringType.createReference("string::slice(${text.getReference()},${surrealJson.encodeToString(Long.serializer(), start)},${length.getReference()})")
    fun slice(text: StringType, start: LongType, length: LongType) = stringType.createReference("string::slice(${text.getReference()},${start.getReference()},${length.getReference()})")

    fun slug(value: StringType) = stringType.createReference("string::slug(${value.getReference()})")

    fun split(value: StringType, deliminator: StringType) = stringType.createReference("string::split(${value.getReference()},${deliminator.getReference()})")
    fun split(value: String, deliminator: StringType) = stringType.createReference("string::split(${surrealJson.encodeToString(String.serializer(), value)},${deliminator.getReference()})")
    fun split(value: StringType, deliminator: String) = stringType.createReference("string::split(${value.getReference()},${surrealJson.encodeToString(String.serializer(), deliminator)})")
    fun trim(value: StringType) = stringType.createReference("string::trim(${value.getReference()})")
    fun words(value: StringType) = ListType(stringType, "string::words(${value.getReference()})")
}
