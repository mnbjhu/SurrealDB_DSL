package uk.gibby.dsl.functions

import uk.gibby.dsl.types.*


fun rand() = doubleType.createReference("rand()")
object Rand {

    fun bool() = booleanType.createReference("rand::bool()")
    fun float() = doubleType.createReference("rand::float()")
    fun <T, U: Reference<T>>enum(vararg values: U) = booleanType.createReference("rand::enum(${values.joinToString(",") { it.getReference() }})")
    fun guid() = stringType.createReference("rand::guid()")
    fun uuid() = stringType.createReference("rand::uuid()")
    fun int() = longType.createReference("rand::int()")
    fun string() = doubleType.createReference("rand::string()")
    fun string(length: LongType) = doubleType.createReference("rand::string(${length.getReference()})")
    fun string(length: Long) = doubleType.createReference("rand::string($length)")
    fun string(minLength: LongType, maxLength: LongType) = doubleType.createReference("rand::string(${minLength.getReference()},${maxLength.getReference()})")
    fun string(minLength: Long, maxLength: LongType) = doubleType.createReference("rand::string($minLength,${maxLength.getReference()})")
    fun string(minLength: LongType, maxLength: Long) = doubleType.createReference("rand::string(${minLength.getReference()},$maxLength)")
    fun string(minLength: Long, maxLength: Long) = doubleType.createReference("rand::string($minLength,$maxLength)")

    fun time() = dateTimeType.createReference("rand::time()")

    fun time(minTime: LongType, maxTime: LongType) = dateTimeType.createReference("rand::time(${minTime.getReference()},${maxTime.getReference()})")
    fun time(minTime: Long, maxTime: LongType) = dateTimeType.createReference("rand::time($minTime,${maxTime.getReference()})")
    fun time(minTime: LongType, maxTime: Long) = dateTimeType.createReference("rand::time(${minTime.getReference()},$maxTime)")
    fun time(minTime: Long, maxTime: Long) = dateTimeType.createReference("rand::time($minTime,$maxTime)")
}