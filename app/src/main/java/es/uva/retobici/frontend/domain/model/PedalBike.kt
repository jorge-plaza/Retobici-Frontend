package es.uva.retobici.frontend.domain.model

data class PedalBike(
    override val id: Int,
    override val unlocked: Boolean
    ): Bike(id, unlocked) {}
