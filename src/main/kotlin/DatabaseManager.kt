import java.sql.Connection
import java.sql.DriverManager
import kotlin.use

class DatabaseManager(private val url: String,
                      private val user: String,
                      private val password: String)

    {
    private var connection: Connection? = null

    init {
        connect()
    }

    private fun connect() {
            connection = DriverManager.getConnection(url, user, password)
    }

    fun getConnection(): Connection? {
        return connection
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


    fun addReservation(accountId: Int, eventId: Int, row: Int, col: Int) {
        val reservationSql = "INSERT INTO reservations (account_id, event_id, seat_row, seat_col) VALUES (?, ?, ?, ?)"
        val updateSeatSql = "UPDATE seats SET is_available = FALSE WHERE event_id = ? AND `row` = ? AND `col` = ?"

        try {
            connection?.autoCommit = false

            connection?.prepareStatement(reservationSql)?.use { statement ->
                statement.setInt(1, accountId)
                statement.setInt(2, eventId)
                statement.setInt(3, row)
                statement.setInt(4, col)
                statement.executeUpdate()
            }

            connection?.prepareStatement(updateSeatSql)?.use { statement ->
                statement.setInt(1, eventId)
                statement.setInt(2, row)
                statement.setInt(3, col)
                statement.executeUpdate()
            }

            connection?.commit()
            println("Rezerwacja dodana pomyślnie dla użytkownika $accountId na wydarzenie $eventId, rząd $row, miejsce $col.")
        } catch (e: Exception) {
            connection?.rollback()
            println("Błąd podczas dodawania rezerwacji: ${e.message}")
        } finally {
            connection?.autoCommit = true
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
            val deleteReservationSql = """
        DELETE r 
        FROM reservations r 
        JOIN events e ON r.event_id = e.id 
        WHERE e.name = ? AND r.seat_row = ? AND r.seat_col = ?
    """
            val updateSeatSql = """
        UPDATE seats s
        JOIN events e ON s.event_id = e.id
        SET s.is_available = TRUE
        WHERE e.name = ? AND s.row = ? AND s.col = ?
    """
            connection?.autoCommit = false
            connection?.prepareStatement(deleteReservationSql)?.use { statement ->
                    statement.setString(1, eventName)
                    statement.setInt(2, seat.row)
                    statement.setInt(3, seat.col)
                    statement.executeUpdate()
            }
            connection?.prepareStatement(updateSeatSql)?.use { statement ->
                    statement.setString(1, eventName)
                    statement.setInt(2, seat.row)
                    statement.setInt(3, seat.col)
                    statement.executeUpdate()
            }
            connection?.commit()
            connection?.autoCommit = true

        }




        fun getSeatsForEvent(eventId: Int): Array<Array<Seat>> {
        val sql = "SELECT `row`, `col`, is_available FROM seats WHERE event_id = ?"
        val seatMap = Array(8) { Array(8) { Seat(true) } }

        connection?.prepareStatement(sql)?.use { statement ->
            statement.setInt(1, eventId)
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                val row = resultSet.getInt("row")
                val col = resultSet.getInt("col")
                val isAvailable = resultSet.getBoolean("is_available")
                seatMap[row][col] = Seat(isAvailable)
            }
        }
        return seatMap
    }



}