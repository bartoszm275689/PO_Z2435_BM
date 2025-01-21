class Account(val username: String, val password: String) {
    val reservations = mutableListOf<Reservation>()
}