package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Stop

interface StopRepository {
    suspend fun getAllStops():List<Stop>
    suspend fun reserveBike(stop: Int, type: String): Stop
    suspend fun lockBike(stop: Int): Stop
}