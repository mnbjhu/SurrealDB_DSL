package e2e.statements

import kotlinx.coroutines.runBlocking
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import schema.ActedIn
import schema.actedIn
import schema.movie
import schema.person
import uk.gibby.dsl.types.eq

open class Relate: Create() {
    @Test
    fun `RELATE $from - $with - $to`(){
        `CREATE $table SET ( $param = $value )`()
        runBlocking {
            db.transaction {
                val johnTravolta by person.selectAll { where(name eq "John Travolta") }
                val samLJackson by person.selectAll { where(name eq "Samuel L. Jackson") }
                val pulpFiction by movie.selectAll { where(title eq "Pulp Fiction") }
                +relate(johnTravolta, actedIn, pulpFiction, ActedIn(actedAs = "Vincent Vega"))
                relate(samLJackson, actedIn, pulpFiction, ActedIn(actedAs = "Jules Winfield"))
            }.first() `should be equal to` ActedIn(actedAs = "Jules Winfield")
        }
    }
}