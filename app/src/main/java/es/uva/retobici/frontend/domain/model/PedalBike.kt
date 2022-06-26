package es.uva.retobici.frontend.domain.model

data class PedalBike(override val bike_id: Int, override val unlocked: Boolean): Bike(bike_id, unlocked) {}
