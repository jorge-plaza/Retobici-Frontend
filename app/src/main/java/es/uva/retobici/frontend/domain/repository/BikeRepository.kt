package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Bike

interface BikeRepository {
    //TODO check if boolean is the best response
    suspend fun unlockBike(bike: Int):Bike

    suspend fun lockBike(bike: Int, stop: Int):Bike
}