package uk.gibby.dsl.annotation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable

@OptIn(ExperimentalSerializationApi::class)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@MetaSerializable
annotation class Relation<T, U>