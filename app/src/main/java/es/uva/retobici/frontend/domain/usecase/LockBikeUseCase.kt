package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.BikeRemoteDataSource
import es.uva.retobici.frontend.domain.model.Bike
import javax.inject.Inject

class LockBikeUseCase @Inject constructor(
    private val remoteDataSource: BikeRemoteDataSource
){

    suspend operator fun invoke(bike: Int, stop: Int): Bike {
        //TODO start tracking the user to get the route
        return remoteDataSource.lockBike(bike,stop)
    }
}