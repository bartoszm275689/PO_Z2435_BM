class Venue(
    val name: String,
    val rows: Int,
    val seatsPerRow: Int
) {
    val seatMap: Array<Array<Seat>> = Array(rows) { row ->
        Array(seatsPerRow) { seatNumber -> Seat(row + 1, seatNumber + 1) }
    }
}

