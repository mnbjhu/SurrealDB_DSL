package uk.gibby.dsl.annotation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable

@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
@MetaSerializable
annotation class Object