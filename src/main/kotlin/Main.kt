import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.time.LocalDateTime

class TheaterBookingApp : Application() {

    private val events = listOf(
        Event("Wydarzenie 1", LocalDateTime.now(), Venue("Sala Główna", 12, 10)),
        Event("Wydarzenie 2", LocalDateTime.now(), Venue("Sala Główna", 12, 10)),
        Event("Wydarzenie 3", LocalDateTime.now(), Venue("Sala Główna", 12, 10))
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

        mainLayout.children.addAll(header)
        mainLayout.children.addAll(eventButtons)
        mainLayout.children.addAll(reservationsHeader, reservationsList)

        val scene = Scene(mainLayout, 400.0, 400.0)
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun updateReservationsList(reservationsList: VBox, primaryStage: Stage) {
        reservationsList.children.clear()
        loggedInAccount?.reservations?.forEach { reservation ->
            val reservationLabel = Label(reservation)
            val cancelButton = Button("Anuluj")
            cancelButton.setOnAction {
                loggedInAccount?.reservations?.remove(reservation)
                updateReservationsList(reservationsList, primaryStage)
            }
            val reservationItem = VBox(5.0, reservationLabel, cancelButton)
            reservationsList.children.add(reservationItem)
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

        val confirmButton = Button("Zarezerwuj")
        confirmButton.isDisable = true
        confirmButton.setOnAction {
            loggedInAccount?.reservations?.add("${event.name} - ${event.date}")
            successLabel.text = "Rezerwacja zakończona."
            confirmButton.isDisable = true
        }

        val backButton = Button("Powrót do menu")
        backButton.setOnAction {
            bookingStage.close()
            showMainMenu(primaryStage)
        }

        val seatMap = createSeatMap(event, confirmButton)

        bookingLayout.children.addAll(header, eventDetails, seatMap, successLabel, confirmButton, backButton)

        val scene = Scene(bookingLayout, 800.0, 700.0)
        bookingStage.title = "Rezerwacja miejsc - ${event.name}"
        bookingStage.scene = scene
        bookingStage.show()

        primaryStage.hide()
    }

                private fun createSeatMap(event: Event, confirmButton: Button): GridPane {
        val seatGrid = GridPane()
        seatGrid.hgap = 5.0
        seatGrid.vgap = 5.0
        seatGrid.padding = Insets(10.0)

        event.venue.seatMap.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { seatIndex, seat ->
                val seatButton = Button("${rowIndex + 1}-${seatIndex + 1}")
                seatButton.style = if (seat.isAvailable) {
                    "-fx-background-color: green; -fx-text-fill: white;"
                } else {
                    "-fx-background-color: red; -fx-text-fill: white;"
                }

                seatButton.setOnAction {
                    if (seat.isAvailable) {
                        seat.isAvailable = false
                        seatButton.style = "-fx-background-color: red; -fx-text-fill: white;"
                    }

                    val anyAvailable = event.venue.seatMap.flatten().any { it.isAvailable }
                    confirmButton.isDisable = !anyAvailable
                }

                seatGrid.add(seatButton, seatIndex, rowIndex)
            }
        }

        return seatGrid
    }
}

fun main() {
    Application.launch(TheaterBookingApp::class.java)
}
