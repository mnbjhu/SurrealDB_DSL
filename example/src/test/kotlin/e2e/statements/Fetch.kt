package e2e.statements

import e2e.DatabaseTest
import e2e.statements.Create.Companion.`CREATE $table SET ( $param = $value )`
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import org.amshove.kluent.`should be equal to`
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should contain same`
import org.junit.jupiter.api.Test
import schema.Genre
import schema.movie
import uk.gibby.dsl.model.Linked

class Fetch: DatabaseTest() {
    @Test
    fun basicFetch() {
        `CREATE $table SET ( $param = $value )`(db)
        runBlocking {
            db.transaction {
                movie.selectAll { fetch(genres) }
            }.also { it.size `should be equal to` 1 }.first()
                .apply {
                    title `should be equal to` "Pulp Fiction"
                    released `should be equal to` Instant.parse("1994-10-21T00:00:00Z")
                    rating `should be equal to` 8.9
                    genres.size `should be equal to` 3
                    genres.map {
                        it `should be instance of` Linked.Actual::class
                        it as Linked.Actual<*>
                        val genre = it.result
                        genre `should be instance of` Genre::class
                        genre as Genre
                        genre.name
                    } `should contain same` listOf("Action", "Thriller", "Comedy")
                }
        }
    }
}