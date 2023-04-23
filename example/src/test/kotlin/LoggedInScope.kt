import uk.gibby.dsl.core.Scope
import uk.gibby.dsl.functions.Crypto
import uk.gibby.dsl.functions.and
import uk.gibby.dsl.scopes.CodeBlockScope
import uk.gibby.dsl.types.create
import uk.gibby.dsl.types.eq
import kotlin.time.Duration

object LoggedInScope: Scope<
        UserCredentials,
        UserCredentialsObject,
        UserCredentials,
        UserCredentialsObject,
        User,
        UserRecord
        >(
    "logged_in_scope",
    Duration.parse("4h"),
    UserCredentialsType,
    UserCredentialsType,
    user,
) {
    override fun signUp(auth: UserCredentialsObject) = user.create {
        username setAs auth.username
        password setAs Crypto.Argon2.generate(auth.password)
        products setAs listOf()
    }

    override fun signIn(auth: UserCredentialsObject) = user.selectAll {
        where(
            (username eq auth.username)
                    and
            Crypto.Argon2.compare(password, auth.password)
        )
    }
}