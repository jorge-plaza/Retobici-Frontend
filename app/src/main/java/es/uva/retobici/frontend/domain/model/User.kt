package es.uva.retobici.frontend.domain.model

data class User (
    val id: Int,
    val email: String,
    val token: String,
    val points: Int
    )
