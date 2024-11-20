import java.time.LocalDate

data class Wydarzenie(
    val id: Int,
    val nazwa: String,
    val data: LocalDate,
    val miejsce: String,
    val cenaBiletu: Double
)