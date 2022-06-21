package es.uva.retobici.frontend.domain.model

abstract class Bike(
    open val bike_id: Int,
    open val unlocked: Boolean
    ) {}
