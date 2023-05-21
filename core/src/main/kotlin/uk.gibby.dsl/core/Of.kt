package uk.gibby.dsl.core

import kotlinx.serialization.encodeToString
import uk.gibby.dsl.driver.surrealJson
import uk.gibby.dsl.types.Reference


inline infix fun <reified T, U: Reference<T>>U.of(value: T): U = createReference(surrealJson.encodeToString(value)) as U