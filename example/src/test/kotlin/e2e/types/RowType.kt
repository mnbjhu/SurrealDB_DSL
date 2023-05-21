package e2e.types

import e2e.DatabaseTest
import e2e.statements.Relate.Companion.`RELATE $from - $with - $to`
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain same`
import org.junit.jupiter.api.Test
import schema.*
import uk.gibby.dsl.functions.Array
import uk.gibby.dsl.model.rows.Row3
import uk.gibby.dsl.types.*
import uk.gibby.dsl.model.rows.Row2
import uk.gibby.dsl.types.row.rowOf

class RowType: DatabaseTest() {

    @Test
    fun `Select 2d row`() {
        `RELATE $from - $with - $to`(db)
        runBlocking {
            val result = db.transaction {
                person.select {
                    rowOf(
                        name,
                        `o-→`(actedIn).`o-→`(movie).linked { title }
                    )
                }
            }
            result `should contain same` listOf(
                Row2("Samuel L. Jackson", listOf("Pulp Fiction")),
                Row2("John Travolta", listOf("Pulp Fiction"))
            )
        }
    }



    @Test
    fun `LET 3d row`() {
        `RELATE $from - $with - $to`(db)
        runBlocking {
            val result = db.transaction {
                val myRow by person.select {
                    rowOf(
                        name,
                        `o-→`(actedIn).`o-→`(movie).linked { title },
                        dateOfBirth
                    )
                }
                Array.len(myRow)
            }
            result `should be equal to` 2
        }
    }



    @Test
    fun `Select 3d row`() {
        `RELATE $from - $with - $to`(db)
        runBlocking {
            val result = db.transaction {
                person.select {
                    rowOf(
                        name,
                        `o-→`(actedIn).`o-→`(movie).linked { title },
                        dateOfBirth
                    )
                }
            }
            result `should contain same` listOf(
                Row3("Samuel L. Jackson", listOf("Pulp Fiction"), Instant.parse("1948-12-21T00:00:00Z")),
                Row3("John Travolta", listOf("Pulp Fiction"), Instant.parse("1954-02-18T00:00:00Z"))
            )
        }
    }



    @Test
    fun `LET 2d row`() {
        `RELATE $from - $with - $to`(db)
        runBlocking {
            val result = db.transaction {
                val myRow by person.select {
                    rowOf(
                        name,
                        `o-→`(actedIn).`o-→`(movie).linked { title }
                    )
                }
                Array.len(myRow)
            }
            result `should be equal to` 2
        }
    }
}