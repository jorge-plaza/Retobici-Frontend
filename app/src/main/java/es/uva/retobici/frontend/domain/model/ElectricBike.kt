package es.uva.retobici.frontend.domain.model

data class ElectricBike(
    override val id: Int,
    var battery: Float,
): Bike(id) {}
