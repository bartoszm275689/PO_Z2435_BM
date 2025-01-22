import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

class TheaterBookingApp : Application() {
    private val databaseManager = DatabaseManager(
        url = "jdbc:mysql://localhost:3306/theater_booking",
        user = "root",
        password = "panzer1979"
    )
    val connection = databaseManager.getConnection()

    private val accounts = mutableMapOf<String, Account>()
    private var loggedInAccount: Account? = null

    override fun start(primaryStage: Stage) {
        showLoginWindow(primaryStage)
    }

    private fun showLoginWindow(primaryStage: Stage) {
        val loginStage = Stage()
        loginStage.title = "Logowanie"

        val loginLayout = VBox(10.0)
        loginLayout.padding = Insets(10.0)

        val header = Label("Zaloguj się")
        header.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val usernameField = TextField()
        usernameField.promptText = "Nazwa użytkownika"

        val passwordField = PasswordField()
        passwordField.promptText = "Hasło"

        val loginButton = Button("Zaloguj")
        val registerButton = Button("Zarejestruj")
        val errorLabel = Label("")
        errorLabel.style = "-fx-text-fill: red;"

        loginButton.setOnAction {
            val username = usernameField.text
            val password = passwordField.text
            val account = databaseManager.getAccount(username)

            if (account != null && account.password == password) {
                loggedInAccount = account
                loginStage.close()
                showMainMenu(primaryStage)
            } else {
                errorLabel.text = "Niepoprawna nazwa użytkownika lub hasło."
            }
        }

        registerButton.setOnAction {
            loginStage.close()
            showRegistrationWindow(primaryStage)
        }

        loginLayout.children.addAll(header, usernameField, passwordField, loginButton, registerButton, errorLabel)

        val scene = Scene(loginLayout, 300.0, 200.0)
        loginStage.scene = scene
        loginStage.show()
    }

    private fun showRegistrationWindow(primaryStage: Stage) {
        val registrationStage = Stage()
        registrationStage.title = "Rejestracja"

        val registrationLayout = VBox(10.0)
        registrationLayout.padding = Insets(10.0)

        val header = Label("Zarejestruj się")
        header.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val usernameField = TextField()
        usernameField.promptText = "Nazwa użytkownika"

        val passwordField = PasswordField()
        passwordField.promptText = "Hasło"

        val registerButton = Button("Zarejestruj")
        val errorLabel = Label("")
        errorLabel.style = "-fx-text-fill: red;"

        registerButton.setOnAction {
            val username = usernameField.text
            val password = passwordField.text

            if (username.isNotBlank() && password.isNotBlank()) {
                if (databaseManager.getAccount(username) != null) {
                    errorLabel.text = "Nazwa użytkownika jest już zajęta."
                } else {
                    databaseManager.addAccount(username, password)
                    registrationStage.close()
                    showLoginWindow(primaryStage)
                }
            } else {
                errorLabel.text = "Wszystkie pola muszą być wypełnione."
            }
        }

        registrationLayout.children.addAll(header, usernameField, passwordField, registerButton, errorLabel)

        val scene = Scene(registrationLayout, 300.0, 200.0)
        registrationStage.scene = scene
        registrationStage.show()
    }

    private fun showMainMenu(primaryStage: Stage) {
        primaryStage.title = "Lista wydarzeń"

        val mainLayout = VBox(10.0)
        mainLayout.padding = Insets(10.0)

        val loggedInUserLabel = Label("Zalogowany jako: ${loggedInAccount?.username}")
        loggedInUserLabel.style = "-fx-font-size: 14px; -fx-font-weight: bold;"

        val logoutButton = Button("Wyloguj")
        logoutButton.setOnAction {
            loggedInAccount = null
            primaryStage.close()
            showLoginWindow(Stage())
        }

        val header = Label("Wybierz wydarzenie")
        header.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val eventButtons = databaseManager.getEvents().map { event ->
            Button(event.name).apply {
                setOnAction {
                    openBookingWindow(event, primaryStage)
                }
            }
        }

        val reservationHeader = Label("Twoje rezerwacje:")
        reservationHeader.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val reservationList = VBox(5.0)
        loggedInAccount?.let { account ->
            val reservations = databaseManager.getUserReservations(account.username)
            reservations.forEach { reservation ->
                val seatLabel = Label("Wydarzenie: ${reservation.eventName}, Rząd: ${reservation.seats[0].row + 1}, Miejsce: ${reservation.seats[0].col + 1}")
                val cancelButton = Button("Anuluj").apply {
                    setOnAction {
                        databaseManager.cancelReservation(reservation.eventName, reservation.seats[0])
                        showMainMenu(primaryStage)
                    }
                }
                val reservationEntry = VBox(10.0, seatLabel, cancelButton)
                reservationList.children.add(reservationEntry)
            }
        }

        mainLayout.children.addAll(loggedInUserLabel, logoutButton, header)
        mainLayout.children.addAll(eventButtons)
        mainLayout.children.addAll(reservationHeader, reservationList)

        val scene = Scene(mainLayout, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()
    }


    private fun openBookingWindow(event: Event, primaryStage: Stage) {
        val bookingStage = Stage()
        bookingStage.title = "Rezerwacja - ${event.name}"

        val bookingLayout = VBox(10.0)
        bookingLayout.padding = Insets(10.0)

        val header = Label("Rezerwacja miejsc na: ${event.name}")
        header.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val seatMap = GridPane()
        seatMap.hgap = 5.0
        seatMap.vgap = 5.0

        val venue = event.venue
        val selectedSeats = mutableListOf<SeatPosition>()

        loadSeats(event.id, seatMap, selectedSeats)

        val confirmButton = Button("Potwierdź rezerwację")
        confirmButton.style = "-fx-font-size: 14px; -fx-font-weight: bold;"

        confirmButton.setOnAction {
            if (selectedSeats.isEmpty()) {
                val alert = Alert(Alert.AlertType.WARNING)
                alert.title = "Brak wybranych miejsc"
                alert.headerText = null
                alert.contentText = "Nie wybrałeś żadnych miejsc do rezerwacji!"
                alert.showAndWait()
            } else {
                loggedInAccount?.let { account ->
                    for (seat in selectedSeats) {
                        databaseManager.addReservation(
                            accountId = account.id,
                            eventId = event.id,
                            row = seat.row,
                            col = seat.col
                        )
                        venue.seatMap[seat.row][seat.col].isAvailable = false
                    }

                    val alert = Alert(Alert.AlertType.INFORMATION)
                    alert.title = "Rezerwacja potwierdzona"
                    alert.headerText = null
                    alert.contentText = "Pomyślnie zarezerwowano miejsca!"
                    alert.showAndWait()

                    bookingStage.close()
                    showMainMenu(primaryStage)
                } ?: run {
                    val alert = Alert(Alert.AlertType.ERROR)
                    alert.title = "Błąd"
                    alert.headerText = null
                    alert.contentText = "Musisz być zalogowany, aby zarezerwować miejsca."
                    alert.showAndWait()
                }
            }
        }

        val backButton = Button("Powrót")
        backButton.style = "-fx-font-size: 14px; -fx-font-weight: bold;"
        backButton.setOnAction {
            bookingStage.close()
            showMainMenu(primaryStage)
        }

        bookingLayout.children.addAll(header, seatMap, confirmButton, backButton)

        val scene = Scene(bookingLayout, 800.0, 600.0)
        bookingStage.scene = scene
        bookingStage.show()
    }

    private fun loadSeats(eventId: Int, seatMap: GridPane, selectedSeats: MutableList<SeatPosition>) {
        val connection = databaseManager.getConnection()
        val sql = "SELECT `row`, `col`, is_available FROM seats WHERE event_id = ?"
        connection?.prepareStatement(sql)?.use { statement ->
            statement.setInt(1, eventId)
            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val row = resultSet.getInt("row")
                val col = resultSet.getInt("col")
                val isAvailable = resultSet.getBoolean("is_available")

                val seatButton = Button("Rząd ${row + 1}, Miejsce ${col + 1}")
                seatButton.isDisable = !isAvailable

                seatButton.style = if (isAvailable) "-fx-background-color: green;" else "-fx-background-color: red;"

                seatButton.setOnAction {
                    if (isAvailable) {
                        if (selectedSeats.contains(SeatPosition(row, col))) {
                            selectedSeats.remove(SeatPosition(row, col))
                            seatButton.style = "-fx-background-color: green;"
                        } else {
                            selectedSeats.add(SeatPosition(row, col))
                            seatButton.style = "-fx-background-color: yellow;"
                        }
                    }
                }

                seatMap.add(seatButton, col, row)
            }
        }
    }

}

fun main() {
    Application.launch(TheaterBookingApp::class.java)
}