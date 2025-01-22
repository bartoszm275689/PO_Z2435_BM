import java.sql.Connection
import java.sql.DriverManager
import kotlin.use

class DatabaseManager(private val url: String, private val user: String, private val password: String) {
    private var connection: Connection? = null

    init {
        connect()
    }

    private fun connect() {
        try {
            connection = DriverManager.getConnection(url, user, password)
            println("Połączono z bazą danych!")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addAccount(username: String, password: String) {
        val sql = "INSERT INTO accounts (username, password) VALUES (?, ?)"
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, username)
            statement.setString(2, password)
            statement.executeUpdate()
        }
    }

    fun getAccount(username: String): Account? {
        val sql = "SELECT id, username, password FROM accounts WHERE username = ?"
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, username)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                return Account(
                    id = resultSet.getInt("id"),
                    username = resultSet.getString("username"),
                    password = resultSet.getString("password")
                )
            }
        }
        return null
    }


    fun addEvent(event: Event) {
        val sql = "INSERT INTO events (name, date, venue_name, rows, cols) VALUES (?, ?, ?, ?, ?)"
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, event.name)
            statement.setObject(2, event.date)
            statement.setString(3, event.venue.name)
            statement.setInt(4, event.venue.rows)
            statement.setInt(5, event.venue.cols)
            statement.executeUpdate()
        }
    }

    fun getEvents(): List<Event> {
        val sql = "SELECT id, name, date, venue_name, `rows`, `cols` FROM events"
        val events = mutableListOf<Event>()
        connection?.createStatement()?.use { statement ->
            val resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val name = resultSet.getString("name")
                val date = resultSet.getTimestamp("date").toLocalDateTime()
                val venueName = resultSet.getString("venue_name")
                val rows = resultSet.getInt("rows")
                val cols = resultSet.getInt("cols")

                val venue = Venue(venueName, rows, cols)
                events.add(Event(id, name, date, venue))
            }
        }
        return events
    }


    fun getUserReservations(username: String): List<Reservation> {
        val reservations = mutableListOf<Reservation>()
        val sql = """
        SELECT e.name AS event_name, e.date AS event_date, r.seat_row, r.seat_col 
        FROM reservations r 
        JOIN accounts a ON r.account_id = a.id 
        JOIN events e ON r.event_id = e.id 
        WHERE a.username = ?
    """
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, username)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val eventName = resultSet.getString("event_name")
                val eventDate = resultSet.getTimestamp("event_date").toLocalDateTime()
                val seatRow = resultSet.getInt("seat_row")
                val seatCol = resultSet.getInt("seat_col")
                val seat = SeatPosition(seatRow, seatCol)
                reservations.add(Reservation(eventName, eventDate, listOf(seat)))
            }
        }
        return reservations
    }

    fun cancelReservation(eventName: String, seat: SeatPosition) {
        val sql = """
        DELETE r 
        FROM reservations r 
        JOIN events e ON r.event_id = e.id 
        WHERE e.name = ? AND r.seat_row = ? AND r.seat_col = ?
    """
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, eventName)
            statement.setInt(2, seat.row)
            statement.setInt(3, seat.col)
            statement.executeUpdate()
        }
    }


    fun addReservation(accountId: Int, eventId: Int, row: Int, col: Int) {
        val sql = "INSERT INTO reservations (account_id, event_id, seat_row, seat_col) VALUES (?, ?, ?, ?)"
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setInt(1, accountId)
            statement.setInt(2, eventId)
            statement.setInt(3, row)
            statement.setInt(4, col)
            statement.executeUpdate()
        }
    }
}