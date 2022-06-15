package es.uva.retobici.frontend.domain.model

data class Reward(
    val id: Int,
    val title: String,
    val description: String,
    val points: Int,
    val image: String,
)
