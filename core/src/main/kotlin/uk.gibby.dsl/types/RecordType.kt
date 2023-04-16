package uk.gibby.dsl.types

import uk.gibby.dsl.core.Table

interface RecordType<T>: ObjectType<T> {
    val id: RecordLink<T, RecordType<T>>
    override fun <T, U: Reference<T>>attrOf(name: String, type: U): U {
        return if(getReference() == "_")  type.createReference(name) as U
            else type.createReference("${getReference()}.$name") as U
    }
    fun <T, U: RecordType<T>>U.id() = attrOf("id", RecordLink<T, U>("_", this))
    fun <T, U: RecordType<T>>linked(table: Table<T, U>) = RecordLink<T, U>("_", table.recordType)
}