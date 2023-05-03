package e2e.statements

import e2e.DatabaseTest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import schema.*

open class Create: DatabaseTest() {
    @Test
    fun `CREATE $table CONTENT $data`() {
        runBlocking {
            db.transaction {
                +genre.createContent(Genre("Action"))
                +genre.createContent(Genre("Thriller"))
                genre.createContent(Genre("Comedy"))
            }

            db.transaction { person.createContent(Person("John Travolta", dateOfBirth = Instant.parse("1954-02-18T00:00:00Z"))) }
            db.transaction { person.createContent(Person("Samuel L. Jackson", dateOfBirth = Instant.parse("1948-12-21T00:00:00Z"))) }
        }
    }

    @Test
    fun `CREATE $table_id CONTENT $data`() {
        runBlocking {
            db.transaction {
                +genre["action"].createContent(Genre("Action"))
                +genre["thriller"].createContent(Genre("Thriller"))
                genre["comedy"].createContent(Genre("Comedy"))
            }
        }.name `should be equal to` "Comedy"
    }
    @Test
    fun `CREATE $table SET ( $param = $value )`() {
        runBlocking {
            `CREATE $table CONTENT $data`()
            db.transaction {
                movie["pulp_fiction"].create {
                    title setAs "Pulp Fiction"
                    genres setAs genre.select { id }
                    released setAs Instant.parse("1994-10-21T00:00:00Z")
                    rating setAs 8.9
                }
            }.apply {
                title `should be equal to` "Pulp Fiction"
                genres.size `should be equal to` 3
                released `should be equal to` Instant.parse("1994-10-21T00:00:00Z")
                rating `should be equal to` 8.9
            }
        }
    }

    @Test
    fun largeCreate() {
        runBlocking {
            db.transaction {
                val action by genre["action"].createContent(Genre("Action")) { id }
                val thriller by genre["thriller"].createContent(Genre("Thriller")) { id }
                val comedy by genre["comedy"].createContent(Genre("Comedy")) { id }
                movie["pulp_fiction"].create {
                    title setAs "Pulp Fiction"
                    genres setAs listOf(action, thriller, comedy)
                    released setAs Instant.parse("1994-10-21T00:00:00Z")
                    rating setAs 8.9
                }
            }
        }
    }
}