import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import uk.gibby.dsl.annotation.Object
import uk.gibby.dsl.annotation.Table


@Object
data class PGN(
    val event: String,
    val site: String,
    val date: Instant,
    val round: Long,
    val white: String,
    val black: String,
    val turn: Color
)

@Object
enum class Color {
    White, Black
}

@Table
data class Game(val pgn: PGN)
