import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.time.LocalDateTime

class TheaterBookingApp : Application() {

    private val events = listOf(
        Event("Czarodziejski flet", LocalDateTime.parse("2024-12-11T14:30:00"), Venue("Sala Główna", 12, 10)),
        Event("Antygona", LocalDateTime.parse("2024-12-11T14:30:00"), Venue("Sala Główna", 12, 10)),
        Event("Koty", LocalDateTime.parse("2024-12-11T14:30:00"), Venue("Sala Główna", 12, 10))
    )

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

            val account = accounts[username]
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
                if (accounts.containsKey(username)) {
                    errorLabel.text = "Nazwa użytkownika jest już zajęta."
                } else {
                    accounts[username] = Account(username, password)
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

        val eventButtons = events.map { event ->
            Button(event.name).apply {
                setOnAction {
                    openBookingWindow(event, primaryStage)
                }
            }
        }

        val reservationsHeader = Label("Twoje rezerwacje")
        reservationsHeader.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val reservationsList = VBox(10.0)
        updateReservationsList(reservationsList, primaryStage)

        mainLayout.children.addAll(loggedInUserLabel, logoutButton, header)
        mainLayout.children.addAll(eventButtons)
        mainLayout.children.addAll(reservationsHeader, reservationsList)

        val scene = Scene(mainLayout, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun updateReservationsList(reservationsList: VBox, primaryStage: Stage) {
        reservationsList.children.clear()
        loggedInAccount?.reservations?.forEach { reservation ->
            val reservationBox = VBox(5.0)

            val eventInfoLabel = Label("Wydarzenie: ${reservation.eventName}, Data: ${reservation.eventDate}")
            eventInfoLabel.style = "-fx-font-size: 14px; -fx-font-weight: bold;"

            reservationBox.children.add(eventInfoLabel)

            reservation.seats.forEach { pos ->
                val seatLabel = Label("Rząd: ${pos.row+1}, Miejsce: ${pos.col+1}")
                seatLabel.style = "-fx-font-weight: bold;"
                reservationBox.children.add(seatLabel)
            }

            val cancelButton = Button("Anuluj")
            cancelButton.setOnAction {
                val event = events.find { it.name == reservation.eventName && it.date == reservation.eventDate }
                if (event != null) {
                    reservation.seats.forEach { seatPos ->
                        event.venue.seatMap[seatPos.row][seatPos.col].isAvailable = true
                    }
                }
                loggedInAccount?.reservations?.remove(reservation)
                updateReservationsList(reservationsList, primaryStage)
            }

            reservationBox.children.add(cancelButton)
            reservationsList.children.add(reservationBox)
        }
    }

    private fun openBookingWindow(event: Event, primaryStage: Stage) {
        val bookingStage = Stage()

        val bookingLayout = VBox(10.0)
        bookingLayout.padding = Insets(10.0)

        val header = Label("Rezerwacja miejsc na: ${event.name}")
        header.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val eventDetails = VBox(10.0)
        val eventNameLabel = Label("Nazwa wydarzenia: ${event.name}")
        val eventDateLabel = Label("Data: ${event.date}")
        val eventVenueLabel = Label("Sala: ${event.venue.name}")
        eventDetails.children.addAll(eventNameLabel, eventDateLabel, eventVenueLabel)

        val successLabel = Label("")
        successLabel.style = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: green;"

        val chosenSeats = mutableListOf<SeatPosition>()

        val confirmButton = Button("Zarezerwuj")
        confirmButton.isDisable = true
        confirmButton.setOnAction {
            val reservation = Reservation(event.name, event.date, chosenSeats.toList())
            loggedInAccount?.reservations?.add(reservation)
            successLabel.text = "Rezerwacja zakończona. Miejsca zarezerwowane."
            confirmButton.isDisable = true
        }

        val backButton = Button("Powrót do menu")
        backButton.setOnAction {
            bookingStage.close()
            showMainMenu(primaryStage)
        }

        val seatMap = createSeatMap(event, confirmButton, chosenSeats)

        bookingLayout.children.addAll(header, eventDetails, seatMap, successLabel, confirmButton, backButton)

        val scene = Scene(bookingLayout, 1300.0, 600.0)
        bookingStage.title = "Rezerwacja miejsc - ${event.name}"
        bookingStage.scene = scene
        bookingStage.show()

        primaryStage.hide()
    }

    private fun createSeatMap(event: Event, confirmButton: Button, chosenSeats: MutableList<SeatPosition>): GridPane {
        val seatGrid = GridPane()
        seatGrid.hgap = 5.0
        seatGrid.vgap = 5.0
        seatGrid.padding = Insets(10.0)

        event.venue.seatMap.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { seatIndex, seat ->
                val seatButton = Button("Rząd ${rowIndex + 1}, Miejsce ${seatIndex + 1}")
                seatButton.style = if (seat.isAvailable) {
                    "-fx-background-color: green; -fx-text-fill: white;"
                } else {
                    "-fx-background-color: red; -fx-text-fill: white;"
                }

                if (!seat.isAvailable) {
                    seatButton.isDisable = true
                } else {
                    seatButton.setOnAction {
                        seat.isAvailable = false
                        seatButton.style = "-fx-background-color: red; -fx-text-fill: white;"
                        chosenSeats.add(SeatPosition(rowIndex, seatIndex))

                        val anyChosen = chosenSeats.isNotEmpty()
                        confirmButton.isDisable = !anyChosen
                    }
                }

                seatGrid.add(seatButton, seatIndex, rowIndex)
            }
        }

        return seatGrid
    }
}

fun main() {

    val url = "jdbc:mysql://localhost:3306/theater_booking"
    val user = "root"
    val password = "panzer1979"


     Application.launch(TheaterBookingApp::class.java)
}
