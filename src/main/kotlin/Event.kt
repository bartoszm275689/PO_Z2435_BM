import java.time.LocalDateTime
data class Event(
    val id: Int,
    val name: String,
    val date: LocalDateTime,
    val venue: Venue
)
