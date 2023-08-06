package e2e.statements

import e2e.DatabaseTest
import e2e.statements.Create.Companion.`CREATE $table SET ( $param = $value )`
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.Ignore
import org.junit.jupiter.api.Test
import schema.ActedIn
import schema.actedIn
import schema.movie
import schema.person
import uk.gibby.dsl.driver.DatabaseConnection
import uk.gibby.dsl.types.eq

open class Relate: DatabaseTest() {

    @Test
    fun basicRelateTest() {
        `RELATE $from - $with - $to`(db)
    }

    // Think this is due to a surreal bug
    @Test
    @Ignore
    fun createRelation() {
        runBlocking {
            db.transaction {
                val johnTravolta by person.create { name setAs "John Travolta" }
                val pulpFiction by movie.create { title setAs "Pulp Fiction" }
                actedIn.create {
                    `in` setAs johnTravolta.id
                    `out` setAs pulpFiction.id
                    role setAs "Vincent Vega"
                }
            }
        }
    }

    companion object {

        fun `RELATE $from - $with - $to`(db: DatabaseConnection){
            `CREATE $table SET ( $param = $value )`(db)
            runBlocking {
                db.transaction {
                    val johnTravolta by person.selectAll { where(name eq "John Travolta") }
                    val samLJackson by person.selectAll { where(name eq "Samuel L. Jackson") }
                    val pulpFiction by movie.selectAll { where(title eq "Pulp Fiction") }
                    +relate(johnTravolta, actedIn, pulpFiction, ActedIn(role = "Vincent Vega"))
                    relate(samLJackson, actedIn, pulpFiction, ActedIn(role = "Jules Winfield"))
                }.first() `should be equal to` ActedIn(role = "Jules Winfield")
            }
        }
    }

}