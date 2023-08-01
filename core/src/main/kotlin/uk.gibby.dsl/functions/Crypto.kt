package uk.gibby.dsl.functions

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.StringType
import uk.gibby.dsl.types.booleanType
import uk.gibby.dsl.types.stringType

object Crypto {
    fun md5(value: Reference<*>) = value.createReference("md5(${value.getReference()})")
    inline fun <reified T> md5(value: T) = md5(stringType.createReference("sha1(${surrealJson.encodeToString(value)})"))
    fun sha1(value: Reference<*>) = value.createReference("sha1(${value.getReference()})")
    inline fun <reified T> sha1(value: T) = sha1(stringType.createReference("sha1(${surrealJson.encodeToString(value)})"))
    fun sha256(value: Reference<*>) = value.createReference("sha256(${value.getReference()})")
    inline fun <reified T> sha256(value: T) = sha256(stringType.createReference("sha256(${surrealJson.encodeToString(value)})"))
    fun sha512(value: Reference<*>) = value.createReference("sha512(${value.getReference()})")
    inline fun <reified T> sha512(value: T) = sha512(stringType.createReference("sha512(${surrealJson.encodeToString(value)})"))

    object Argon2 {
        fun compare(hash: StringType, pass: StringType) = booleanType.createReference("crypto::argon2::compare(${hash.getReference()},${pass.getReference()})")
        fun compare(hash: String, pass: StringType) = booleanType.createReference("crypto::argon2::compare(${surrealJson.encodeToString(hash)},${pass.getReference()})")
        fun compare(hash: StringType, pass: String) = booleanType.createReference("crypto::argon2::compare(${hash.getReference()},${surrealJson.encodeToString(pass)})")
        fun compare(hash: String, pass: String) = booleanType.createReference("crypto::argon2::compare(${surrealJson.encodeToString(hash)},${surrealJson.encodeToString(pass)})")
        fun generate(pass: StringType) = stringType.createReference("crypto::argon2::generate(${pass.getReference()})")
        fun generate(pass: String) = stringType.createReference("crypto::argon2::generate(${surrealJson.encodeToString(pass)})")
    }

    object Pbkdf2 {
        fun compare(hash: StringType, pass: StringType) = booleanType.createReference("crypto::pbkdf2::compare(${hash.getReference()},${pass.getReference()})")
        fun compare(hash: String, pass: StringType) = booleanType.createReference("crypto::pbkdf2::compare(${surrealJson.encodeToString(hash)},${pass.getReference()})")
        fun compare(hash: StringType, pass: String) = booleanType.createReference("crypto::pbkdf2::compare(${hash.getReference()},${surrealJson.encodeToString(pass)})")
        fun compare(hash: String, pass: String) = booleanType.createReference("crypto::pbkdf2::compare(${surrealJson.encodeToString(hash)},${surrealJson.encodeToString(pass)})")
        fun generate(pass: StringType) = stringType.createReference("crypto::pbkdf::generate(${pass.getReference()})")
        fun generate(pass: String) = stringType.createReference("crypto::pbkdf::generate(${surrealJson.encodeToString(pass)})")
    }

    object Scrypt {
        fun compare(hash: StringType, pass: StringType) = booleanType.createReference("crypto::scrypt::compare(${hash.getReference()},${pass.getReference()})")
        fun compare(hash: String, pass: StringType) = booleanType.createReference("crypto::scrypt::compare(${surrealJson.encodeToString(hash)},${pass.getReference()})")
        fun compare(hash: StringType, pass: String) = booleanType.createReference("crypto::scrypt::compare(${hash.getReference()},${surrealJson.encodeToString(pass)})")
        fun compare(hash: String, pass: String) = booleanType.createReference("crypto::scrypt::compare(${surrealJson.encodeToString(hash)},${surrealJson.encodeToString(pass)})")
        fun generate(pass: StringType) = stringType.createReference("crypto::scrypt::generate(${pass.getReference()})")
        fun generate(pass: String) = stringType.createReference("crypto::scrypt::generate(${surrealJson.encodeToString(pass)})")
    }
}