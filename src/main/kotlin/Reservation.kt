data class Reservation(
    val id: Int,
    val event: Event,
    val seats: List<Seat>
)