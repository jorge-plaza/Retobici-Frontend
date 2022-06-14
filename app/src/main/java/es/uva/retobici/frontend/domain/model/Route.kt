package es.uva.retobici.frontend.domain.model

data class Route(
    val id: Int,
    val id_user: Int,
    val bike: Int,
    val initial_stop: Int?,
    var final_stop: Int?,
    val distance: Int?,
    val points: Int?,
    var duration: Int?
)
