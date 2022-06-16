package es.uva.retobici.frontend.domain.model

data class ElectricBike(
    override val id: Int,
    override val unlocked: Boolean,
    var battery: Float,
): Bike(id,unlocked) {}
