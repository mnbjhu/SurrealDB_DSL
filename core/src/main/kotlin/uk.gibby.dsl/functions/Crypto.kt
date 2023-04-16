package uk.gibby.dsl.functions

import uk.gibby.dsl.types.Reference
import uk.gibby.dsl.types.StringType
import uk.gibby.dsl.types.booleanType
import uk.gibby.dsl.types.stringType

object Crypto {
    fun md5(value: Reference<*>) = value.createReference("md5(${value.getReference()})")
    fun sha1(value: Reference<*>) = value.createReference("sha1(${value.getReference()})")
    fun sha256(value: Reference<*>) = value.createReference("sha256(${value.getReference()})")
    fun sha512(value: Reference<*>) = value.createReference("sha512(${value.getReference()})")

    object Argon2 {
        fun compare(hash: StringType, pass: StringType) = booleanType.createReference("crypto::argon2::compare(${hash.getReference()},${pass.getReference()})")
        fun generate(pass: StringType) = stringType.createReference("crypto::argon2::generate(${pass.getReference()})")
    }

    object Pbkdf2 {
        fun compare(hash: StringType, pass: StringType) = booleanType.createReference("crypto::pbkdf2::compare(${hash.getReference()},${pass.getReference()})")
        fun generate(pass: StringType) = stringType.createReference("crypto::pbkdf::generate(${pass.getReference()})")
    }

    object Scrypt {
        fun compare(hash: StringType, pass: StringType) = booleanType.createReference("crypto::scrypt::compare(${hash.getReference()},${pass.getReference()})")
        fun generate(pass: StringType) = stringType.createReference("crypto::scrypt::generate(${pass.getReference()})")
    }
}