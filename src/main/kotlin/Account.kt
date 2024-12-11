data class Account(
    val username: String,
    val password: String,
    val reservations: MutableList<String> = mutableListOf()
)
