package es.uva.retobici.frontend.domain.model

class ElectricBike(
    bike_id: Int,
    unlocked: Boolean,
    var battery: Float,
): Bike(bike_id, unlocked)
