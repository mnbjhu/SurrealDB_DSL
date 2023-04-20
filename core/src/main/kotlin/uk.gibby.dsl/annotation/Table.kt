package uk.gibby.dsl.annotation

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.MetaSerializable


@OptIn(ExperimentalSerializationApi::class)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@MetaSerializable
@Retention(AnnotationRetention.SOURCE)
annotation class Table

