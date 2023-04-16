package uk.gibby.dsl.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class RootAuth(val user: String, val pass: String)