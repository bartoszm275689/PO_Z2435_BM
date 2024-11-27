import javafx.application.Application
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
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

    override fun start(primaryStage: Stage) {
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

        mainLayout.children.addAll(header)
        mainLayout.children.addAll(eventButtons)

        val scene = Scene(mainLayout, 400.0, 300.0)
        primaryStage.scene = scene
        primaryStage.show()
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
            successLabel.text = "Rezerwacja zakończona."
            confirmButton.isDisable = true
        }

        val backButton = Button("Powrót do menu")
        backButton.setOnAction {
            bookingStage.close()
            primaryStage.show()
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
