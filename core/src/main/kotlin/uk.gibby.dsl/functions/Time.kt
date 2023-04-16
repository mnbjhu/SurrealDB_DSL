package uk.gibby.dsl.functions

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.DateTimeType
import uk.gibby.dsl.types.DurationType
import uk.gibby.dsl.types.dateTimeType
import uk.gibby.dsl.types.longType
import kotlin.time.Duration

object Time {

    fun floor(value: DateTimeType, duration: DurationType) = dateTimeType.createReference("time::floor(${value.getReference()}, ${duration.getReference()})")
    fun floor(value: LocalDateTime, duration: DurationType) = dateTimeType.createReference("time::floor(${surrealJson.encodeToString(value)}, ${duration.getReference()})")
    fun floor(value: DateTimeType, duration: Duration) = dateTimeType.createReference("time::floor(${value.getReference()}, ${surrealJson.encodeToString(duration)})")

    fun ceil(value: DateTimeType, duration: DurationType) = dateTimeType.createReference("time::ceil(${value.getReference()}, ${duration.getReference()})")
    fun ceil(value: DateTimeType, duration: Duration) = dateTimeType.createReference("time::ceil(${value.getReference()}, ${surrealJson.encodeToString(duration)})")
    fun ceil(value: LocalDateTime, duration: DurationType) = dateTimeType.createReference("time::ceil(${surrealJson.encodeToString(value)}, ${duration.getReference()})")

    fun round(value: DateTimeType, duration: DurationType) = dateTimeType.createReference("time::round(${value.getReference()}, ${duration.getReference()})")
    fun round(value: DateTimeType, duration: Duration) = dateTimeType.createReference("time::round(${value.getReference()}, ${surrealJson.encodeToString(duration)})")
    fun round(value: LocalDateTime, duration: DurationType) = dateTimeType.createReference("time::round(${surrealJson.encodeToString(value)}, ${duration.getReference()})")

    fun group(value: DateTimeType, group: Group)= dateTimeType.createReference("time::group(${value.getReference()},${group.text})")

    fun unix(value: DateTimeType) = longType.createReference("time::unix(${value.getReference()})")
    fun nano(value: DateTimeType) = longType.createReference("time::nano(${value.getReference()})")
    fun secs(value: DateTimeType) = longType.createReference("time::secs(${value.getReference()})")
    fun mins(value: DateTimeType) = longType.createReference("time::secs(${value.getReference()})")
    fun hour(value: DateTimeType) = longType.createReference("time::hour(${value.getReference()})")
    fun day(value: DateTimeType) = longType.createReference("time::day(${value.getReference()})")
    fun week(value: DateTimeType) = longType.createReference("time::week(${value.getReference()})")
    fun month(value: DateTimeType) = longType.createReference("time::month(${value.getReference()})")
    fun year(value: DateTimeType) = longType.createReference("time::year(${value.getReference()})")
    fun wday(value: DateTimeType) = longType.createReference("time::wday(${value.getReference()})")
    fun yday(value: DateTimeType) = longType.createReference("time::wday(${value.getReference()})")

    fun now() = dateTimeType.createReference("time::now()")

    enum class Group(val text: String){
        Second("second"),
        Minute("minute"),
        Hour("hour"),
        Day("day"),
        Month("month"),
        Year("year")
    }

}

