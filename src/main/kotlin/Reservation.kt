import java.time.LocalDateTime

data class Reservation(val eventName: String, val eventDate: LocalDateTime, val seats: List<SeatPosition>)
