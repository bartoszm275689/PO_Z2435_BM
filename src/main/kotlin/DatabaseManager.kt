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
        val sql = "SELECT * FROM accounts WHERE username = ?"
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setString(1, username)
            val resultSet = statement.executeQuery()
            if (resultSet.next()) {
                return Account(
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
        val sql = "SELECT * FROM events"
        val events = mutableListOf<Event>()
        connection?.createStatement()?.use { statement ->
            val resultSet = statement.executeQuery(sql)
            while (resultSet.next()) {
                val name = resultSet.getString("name")
                val date = resultSet.getTimestamp("date").toLocalDateTime()
                val venueName = resultSet.getString("venue_name")
                val rows = resultSet.getInt("rows")
                val cols = resultSet.getInt("cols")

                val venue = Venue(venueName, rows, cols)
                events.add(Event(name, date, venue))
            }
        }
        return events
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