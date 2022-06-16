package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Stop

interface StopRepository {
    suspend fun getAllStops():List<Stop>
}