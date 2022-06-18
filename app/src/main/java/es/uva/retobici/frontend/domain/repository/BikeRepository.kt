package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Stop

interface BikeRepository {
    suspend fun unlockBike(bike: Int):Bike
    suspend fun reserveBike(stop: Int): Stop
}