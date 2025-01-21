class Venue(val name: String, val rows: Int, val cols: Int) {
    val seatMap: List<List<Seat>> = List(rows) { List(cols) { Seat() } }
}