data class Seat (
    val section: String,
    val row: Int,
    val number: Int,
    var isAvailable: Boolean = true
)