package es.uva.retobici.frontend.data.repositories

import es.uva.retobici.frontend.data.source.api.BikeAPI
import es.uva.retobici.frontend.data.source.dto.toBikeModel
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.repository.BikeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BikeRemoteDataSource @Inject constructor(
    private val api:BikeAPI
): BikeRepository {
    override suspend fun unlockBike(bike: Int): Bike {
        return withContext(Dispatchers.IO){
            val response = api.postUnlockBike(bike)
            // ?: if response is null empty list
            //response.body()?.map { it.toStopModel() } ?: emptyList()
            //TODO check the nullable response
            response.body()!!.toBikeModel()
        }
    }

    override suspend fun lockBike(bike: Int, stop: Int): Bike {
        return withContext(Dispatchers.IO){
            //TODO check if the id must be called here or before
            val response = api.postLockBike(bike, stop)
            // ?: if response is null empty list
            //response.body()?.map { it.toStopModel() } ?: emptyList()
            //TODO check the nullable response
            response.body()!!.toBikeModel()
        }
    }

}