package e2e.statements

import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should contain same`
import org.amshove.kluent.`should match`
import org.junit.jupiter.api.Test
import schema.*
import uk.gibby.dsl.model.Linked
import uk.gibby.dsl.types.STAR
import uk.gibby.dsl.types.eq
import uk.gibby.dsl.types.`o-→`
import uk.gibby.dsl.types.`←-o`

class Select: Relate() {

    @Test
    fun selectAllFromTableId() {
        `SELECT all FROM $table`()
    }

    @Test
    fun selectProjectionFromTableId() {
        `SELECT $projection FROM $table`()
    }

    @Test
    fun selectRelatedTableIds() {
        `SELECT $from - $with - $to FROM $table`()
    }

    @Test
    fun selectRelatedTable() {
        `SELECT $from - $with - $to all FROM $table`()
    }

    @Test
    fun selectRelationId() {
        `SELECT $from - $with FROM $table`()
    }

    @Test
    fun selectPath() {
        `SELECT $path FROM $table`()
    }

    fun `SELECT all FROM $table`(){
        `RELATE $from - $with - $to`()
        runBlocking {
            db.transaction {
                movie.selectAll()
            }
        }.also { it.size `should be equal to` 1 }.first()
            .apply {
                title `should be equal to` "Pulp Fiction"
                genres.size `should be equal to` 3
                released `should be equal to` Instant.parse("1994-10-21T00:00:00Z")
                rating `should be equal to` 8.9
            }
    }
    fun `SELECT $projection FROM $table`(){
        `RELATE $from - $with - $to`()
        runBlocking {
            db.transaction {
                movie.select { title }
            }
        } `should contain same` listOf("Pulp Fiction")
    }

    fun `SELECT $from - $with - $to FROM $table`(){
        `RELATE $from - $with - $to`()
        runBlocking {
            db.transaction {
                person.select {
                    where(name eq "John Travolta")
                    `o-→`(actedIn).`o-→`(movie)
                }
            }.first().first().apply {
                `should be instance of`<Linked.Reference<*>>()
                id `should match` "^Movie:.*".toRegex()
            }
        }
    }

    fun `SELECT $from - $with - $to all FROM $table`(){
        `RELATE $from - $with - $to`()
        runBlocking {
            db.transaction {
                person.select {
                    where(name eq "John Travolta")
                    `o-→`(actedIn).`o-→`(movie).STAR
                }
            }.also { it.size `should be equal to` 1 }
                .first().first()
                .apply {
                    title `should be equal to` "Pulp Fiction"
                    genres.size `should be equal to` 3
                    released `should be equal to` Instant.parse("1994-10-21T00:00:00Z")
                    rating `should be equal to` 8.9
                }
        }
    }

    fun `SELECT $from - $with FROM $table`(){
        `RELATE $from - $with - $to`()
        runBlocking {
            db.transaction {
                person.select {
                    where(name eq "John Travolta")
                    `o-→`(actedIn)
                }
            }.also { it.size `should be equal to` 1 }.first().first().apply {
                `should be instance of`<Linked.Reference<*>>()
                this as Linked.Reference<*>
                id `should match` "^ActedIn:.*$".toRegex()
            }
        }
    }

    fun `SELECT $path FROM $table`(){
        `RELATE $from - $with - $to`()
        runBlocking {
            db.transaction {
                val quentin by person.createContent(
                    Person(
                        name = "Quentin Tarantino",
                        dateOfBirth = Instant.parse("1963-03-27T00:00:00Z")
                    )
                )
                val pulpFiction by movie.selectAll { where( title eq "Pulp Fiction") }
                +relate(quentin, directed, pulpFiction, Directed())
                person.select {
                    where(name eq "John Travolta")
                    `o-→`(actedIn).`o-→`(movie).`←-o`(directed).`←-o`(person).STAR
                }
            }.also { println(it) }
        }
    }
}