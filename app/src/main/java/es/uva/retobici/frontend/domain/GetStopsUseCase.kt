package es.uva.retobici.frontend.domain

import es.uva.retobici.frontend.data.models.Stop
import es.uva.retobici.frontend.data.repositories.StopRepository

class GetStopsUseCase {

    private val repository = StopRepository()

    suspend operator fun invoke(): List<Stop> {
        return repository.getAllStops()
    }
}