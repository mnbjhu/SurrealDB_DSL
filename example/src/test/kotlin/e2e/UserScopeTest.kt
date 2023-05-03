package e2e

import kotlinx.coroutines.runBlocking
import schema.SurrealTvSchema
import schema.UserSignUpDetails
import schema.userScope

abstract class UserScopeTest: DatabaseTest(){
    override suspend fun setupDatabase() {
        super.setupDatabase()
        runBlocking {
            db.signUp(
                namespaceName, databaseName, userScope, UserSignUpDetails(
                    testUserCredentials,
                    testUserDetails
                )
            )
            db.use(namespaceName, databaseName)
            db.define(SurrealTvSchema)
            db.invalidate()
            db.use(namespaceName, databaseName)
        }
    }
}