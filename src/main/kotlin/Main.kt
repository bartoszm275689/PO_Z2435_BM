import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import java.sql.Connection
import java.sql.DriverManager
import java.time.LocalDateTime



class TheaterBookingApp : Application() {
    private val databaseManager = DatabaseManager(
        url = "jdbc:mysql://localhost:3306/theater_booking",
        user = "root",
        password = "panzer1979"
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

        mainLayout.children.addAll(loggedInUserLabel, logoutButton, header)
        mainLayout.children.addAll(eventButtons)

        val scene = Scene(mainLayout, 800.0, 600.0)
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun openBookingWindow(event: Event, primaryStage: Stage) {
        val bookingStage = Stage()
        val bookingLayout = VBox(10.0)
        bookingLayout.padding = Insets(10.0)

        val header = Label("Rezerwacja miejsc na: ${event.name}")
        header.style = "-fx-font-size: 16px; -fx-font-weight: bold;"

        val seatMap = GridPane()
        seatMap.hgap = 5.0
        seatMap.vgap = 5.0

        bookingLayout.children.addAll(header, seatMap)

        val scene = Scene(bookingLayout, 600.0, 400.0)
        bookingStage.scene = scene
        bookingStage.title = "Rezerwacja - ${event.name}"
        bookingStage.show()
    }
}

fun main() {
    Application.launch(TheaterBookingApp::class.java)
}
