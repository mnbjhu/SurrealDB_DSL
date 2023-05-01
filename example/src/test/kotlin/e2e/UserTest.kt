package e2e

import LoggedInScope
import NewSchema
import UserCredentials
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import user

class UserTest: RootTest() {
    init {
        runBlocking {
            db.define(NewSchema)
            db.invalidate()
        }
    }

    @Test
    fun signUpTest() {
        runBlocking {
            db.signUp("test", "test", LoggedInScope, UserCredentials("mnbjhu", "testpass"))
            db.transaction { user.selectAll() } `should be equal to` listOf()
        } `should be equal to` listOf()
    }
}