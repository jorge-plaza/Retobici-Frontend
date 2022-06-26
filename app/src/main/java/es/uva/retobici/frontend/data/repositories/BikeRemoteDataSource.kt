package es.uva.retobici.frontend.data.repositories

import android.util.Log
import es.uva.retobici.frontend.data.UserPreferences
import es.uva.retobici.frontend.data.source.api.BikeAPI
import es.uva.retobici.frontend.data.source.dto.toBikeModel
import es.uva.retobici.frontend.data.source.dto.toStopModel
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.domain.repository.BikeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BikeRemoteDataSource @Inject constructor(
    private val api:BikeAPI,
    private val userPreferences: UserPreferences
): BikeRepository {
    override suspend fun unlockBike(bike: Int): Bike {
        return withContext(Dispatchers.IO){
            val token = userPreferences.authToken.first()
            val response = api.postUnlockBike(bike, "Bearer ${token!!}")
            // ?: if response is null empty list
            //response.body()?.map { it.toStopModel() } ?: emptyList()
            response.body()!!.toBikeModel()
        }
    }

}