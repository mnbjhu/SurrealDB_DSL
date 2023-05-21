package e2e.statements

import e2e.DatabaseTest
import e2e.statements.Create.Companion.`CREATE $table SET ( $param = $value )`
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should contain same`
import org.junit.Ignore
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import schema.movie
import uk.gibby.dsl.functions.plus

class Update: DatabaseTest(){

    @Test
    fun `UPDATE $table ( SET $field = $value ) RETURN AFTER`() {
        `CREATE $table SET ( $param = $value )`(db)
        runBlocking {
            db.transaction {
                movie.update { title setAs title + " - but updated!"; After }
            }.map { it.title } `should contain same` listOf("Pulp Fiction - but updated!")
        }
    }

    @Test
    fun `UPDATE $table ( SET $field = $value ) RETURN BEFORE`() {
        `CREATE $table SET ( $param = $value )`(db)
        runBlocking {
            db.transaction {
                movie.update { title setAs title + " - but updated!"; Before }
            }.map { it.title } `should contain same` listOf("Pulp Fiction")
        }
    }
    @Test
    fun `UPDATE $table ( SET $field = $value ) RETURN NONE`() {
        `CREATE $table SET ( $param = $value )`(db)
        runBlocking {
            db.transaction {
                movie.update { title setAs title + " - but updated!" }
            }.size `should be equal to` 0
        }
    }

    @Test
    fun `UPDATE $table ( SET $field = $value ) RETURN $field`() {
        `CREATE $table SET ( $param = $value )`(db)
        runBlocking {
            db.transaction {
                movie.update { title setAs title + " - but updated!"; title }
            } `should contain same` listOf("Pulp Fiction - but updated!")
        }
    }
}