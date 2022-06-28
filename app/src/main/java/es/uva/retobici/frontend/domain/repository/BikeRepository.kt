package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Bike

interface BikeRepository {
    suspend fun unlockBike(bike: Int):Bike
}