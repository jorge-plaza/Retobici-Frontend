package es.uva.retobici.frontend.data.repositories

import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.data.models.StopProvider
import es.uva.retobici.frontend.data.source.api.StopAPI
import es.uva.retobici.frontend.data.source.dto.toStopModel
import es.uva.retobici.frontend.domain.repository.StopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StopRemoteDataSource @Inject constructor(
    private val api:StopAPI,
    //TODO See what to do with this StopProvider it acts as an small db antes se usaba stopProvider.stops = response
    private val stopProvider: StopProvider
): StopRepository{

    //TODO check if the Flows are needed or with the CRUD operations are enough
    override suspend fun getAllStops(): List<Stop> {
        return withContext(Dispatchers.IO){
            val response = api.getAllStops()
            // ?: if response is null empty list
            response.body()?.map { it.toStopModel() } ?: emptyList()
        }
    }
}