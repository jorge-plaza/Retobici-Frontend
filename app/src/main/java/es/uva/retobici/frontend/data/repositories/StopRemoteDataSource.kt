package es.uva.retobici.frontend.data.repositories

import android.util.Log
import es.uva.retobici.frontend.data.UserPreferences
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.data.models.StopProvider
import es.uva.retobici.frontend.data.source.api.StopAPI
import es.uva.retobici.frontend.data.source.dto.toStopModel
import es.uva.retobici.frontend.domain.repository.StopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class StopRemoteDataSource @Inject constructor(
    private val api:StopAPI,
    private val userPreferences: UserPreferences
): StopRepository{

    //TODO check if the Flows are needed or with the CRUD operations are enough
    override suspend fun getAllStops(): List<Stop> {
        return withContext(Dispatchers.IO){
            val response = api.getAllStops()
            // ?: if response is null empty list
            response.body()?.map { it.toStopModel() } ?: emptyList()
        }
    }

    override suspend fun reserveBike(stop: Int, type: String): Stop {
        return withContext(Dispatchers.IO) {
            val token = userPreferences.authToken.first()
            val response = api.postReserveBike("Bearer ${token!!}",stop,type)
            response.body()!!.toStopModel()
        }
    }

    override suspend fun lockBike(stop: Int): Stop {
        return withContext(Dispatchers.IO) {
            val token = userPreferences.authToken.first()
            val response = api.getLockBike("Bearer ${token!!}",stop)
            response.body()!!.toStopModel()
        }
    }
}