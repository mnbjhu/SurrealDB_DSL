package e2e.statements

import e2e.DatabaseTest
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import schema.*
import uk.gibby.dsl.driver.DatabaseConnection

open class Create: DatabaseTest() {

    @Test
    fun createContent(){
        `CREATE $table CONTENT $data`(db)
    }

    @Test
    fun createContentWithId(){
        `CREATE $table_id CONTENT $data`(db)
    }

    @Test
    fun createWithReferences() {
        `CREATE $table SET ( $param = $value )`(db)
    }


    companion object {
        fun `CREATE $table CONTENT $data`(db: DatabaseConnection) {
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

        fun `CREATE $table_id CONTENT $data`(db: DatabaseConnection) {
            runBlocking {
                db.transaction {
                    +genre["action"].createContent(Genre("Action"))
                    +genre["thriller"].createContent(Genre("Thriller"))
                    genre["comedy"].createContent(Genre("Comedy"))
                }
            }.name `should be equal to` "Comedy"
        }
        fun `CREATE $table SET ( $param = $value )`(db: DatabaseConnection) {
            runBlocking {
                `CREATE $table CONTENT $data`(db)
                db.transaction {
                    movie.create {
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

        fun largeCreate(db: DatabaseConnection) {
            runBlocking {
                db.transaction {
                    val action by genre.createContent(Genre("Action")) { id }
                    val thriller by genre.createContent(Genre("Thriller")) { id }
                    val comedy by genre.createContent(Genre("Comedy")) { id }
                    movie.create {
                        title to "Pulp Fiction"
                        genres to listOf(action, thriller, comedy)
                        released to Instant.parse("1994-10-21T00:00:00Z")
                        rating to 8.9
                    }
                }
            }
        }
    }

}