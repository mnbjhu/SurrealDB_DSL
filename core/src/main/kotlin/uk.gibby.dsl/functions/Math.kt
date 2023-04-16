package uk.gibby.dsl.functions


import uk.gibby.dsl.types.*

object Math {
    fun abs(number: DoubleType) = doubleType.createReference("math::abs(${number.getReference()})")
    fun abs(number: LongType) = doubleType.createReference("math::abs(${number.getReference()})")
    fun round(number: DoubleType) = longType.createReference("math::round(${number.getReference()})")
    fun ceil(number: DoubleType) = longType.createReference("math::ceil(${number.getReference()})")
    fun floor(number: DoubleType) = longType.createReference("math::floor(${number.getReference()})")
    fun fixed(number: DoubleType, decimalPlaces: LongType) = longType.createReference("math::fixed(${number.getReference()}, ${decimalPlaces.getReference()})")
    fun fixed(number: Double, decimalPlaces: LongType) = longType.createReference("math::fixed($number, ${decimalPlaces.getReference()})")
    fun fixed(number: DoubleType, decimalPlaces: Long) = longType.createReference("math::fixed(${number.getReference()}, $decimalPlaces)")
    fun max(array: ListType<Double, DoubleType>) = array.inner.createReference("array::max(${array.getReference()})")
    fun min(array: ListType<Double, DoubleType>) = array.inner.createReference("array::min(${array.getReference()})")
    @JvmName("longMin")
    fun max(array: ListType<Long, LongType>) = array.inner.createReference("array::max(${array.getReference()})")

    @JvmName("longMax")
    fun min(array: ListType<Long, LongType>) = array.inner.createReference("array::min(${array.getReference()})")

    fun mean(array: ListType<Double, DoubleType>) = doubleType.createReference("array::mean(${array.getReference()})")
    @JvmName("longMean")
    fun mean(array: ListType<Long, LongType>) = doubleType.createReference("array::mean(${array.getReference()})")
    fun median(array: ListType<Double, DoubleType>) = doubleType.createReference("array::median(${array.getReference()})")
    @JvmName("longMedian")
    fun median(array: ListType<Long, LongType>) = doubleType.createReference("array::median(${array.getReference()})")
    fun product(array: ListType<Double, DoubleType>) = doubleType.createReference("array::product(${array.getReference()})")
    @JvmName("longProduct")
    fun product(array: ListType<Long, LongType>) = doubleType.createReference("array::product(${array.getReference()})")
    fun sqrt(number: DoubleType) = doubleType.createReference("math::sqrt(${number.getReference()})")
    fun sqrt(number: LongType) = doubleType.createReference("math::sqrt(${number.getReference()})")
    fun sum(array: ListType<Double, DoubleType>) = doubleType.createReference("array::sum(${array.getReference()})")
    fun sum(array: ListType<Long, LongType>) = longType.createReference("array::sum(${array.getReference()})")
}