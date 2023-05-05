package schema

import kotlinx.datetime.Instant
import uk.gibby.dsl.annotation.Object
import uk.gibby.dsl.annotation.Relation
import uk.gibby.dsl.annotation.Table
import uk.gibby.dsl.core.scopeOf
import uk.gibby.dsl.functions.*
import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.BooleanType.Companion.FALSE
import uk.gibby.dsl.types.eq
import kotlin.time.Duration

@Table
data class Person(
    val name: String,
    val dateOfBirth: Instant
)

@Table
data class Movie(
    val title: String,
    val released: Instant,
    val genres: List<Linked<Genre>>,
    val rating: Double
)

@Table
data class Genre(val name: String)

@Relation<Person, Movie>
class Directed

@Relation<Person, Movie>
data class ActedIn(val role: String)

@Table
data class User(
    val username: String,
    val passwordHash: String,
    val details: UserDetails,
    val isAdmin: Boolean
)

@Object
data class UserDetails(
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Instant,
    val email: String,
    val phoneNumber: String,
)


@Object
class UserCredentials(
    val username: String,
    val password: String
)

@Object
class UserSignUpDetails(
    val credentials: UserCredentials,
    val details: UserDetails
)




val userScope = scopeOf(
    name = "user_scope",
    sessionDuration = Duration.parse("20m"),
    signupType = UserSignUpDetailsType,
    signInType = UserCredentialsType,
    tokenTable = user,
    signUp = {  auth ->
        user.create {
            username setAs auth.credentials.username
            passwordHash setAs Crypto.Argon2.generate(auth.credentials.password)
            details setAs auth.details
            isAdmin setAs false
        }
    },
    signIn = {  auth ->
        user.selectAll {
            where(
                username eq auth.username
                    and
                Crypto.Argon2.compare(passwordHash, auth.password)
            )
        }
    }
)

val adminScope = scopeOf(
    name = "admin_scope",
    sessionDuration = Duration.parse("20m"),
    signupType = UserSignUpDetailsType,
    signInType = UserCredentialsType,
    tokenTable = user,
    signUp = { user.selectAll { where(FALSE) }[0] },
    signIn = {  auth ->
        user.selectAll {
            where(
                Crypto.Argon2.compare(passwordHash, auth.password) and
                        (username eq auth.username) and isAdmin
            )
        }
    }

)
