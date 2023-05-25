package e2e.statements

import e2e.DatabaseTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test
import schema.*
import uk.gibby.dsl.driver.DatabaseConnection

class Live: DatabaseTest() {
    @Test
    fun basic() {
        runBlocking {
            db.transaction {
                movie.createContent(
                    Movie(
                        title = "The Matrix",
                        rating = 8.7,
                        released = Instant.parse("1999-03-31T00:00:00Z"),
                        genres = listOf(),
                    )
                )
            }
            delay(1000)
            db.liveSelectAll(movie)
            delay(1000)
            db.transaction {
                movie.createContent(
                    Movie(
                        title = "The Matrix",
                        rating = 8.7,
                        released = Instant.parse("1999-03-31T00:00:00Z"),
                        genres = listOf(),
                    )
                )
            }
            delay(10000)
        }
    }

}