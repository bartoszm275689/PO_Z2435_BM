import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

class TheaterBooking : Application() {
    override fun start(stage: Stage) {
        val closeButton = Button("Zamknij").apply {
            setOnAction { stage.close() }
        }

        stage.title = "TheaterBooking"
        stage.scene = Scene(StackPane(closeButton), 300.0, 300.0)
        stage.show()
    }
}

fun main() {
    Application.launch(TheaterBooking::class.java)
}       
