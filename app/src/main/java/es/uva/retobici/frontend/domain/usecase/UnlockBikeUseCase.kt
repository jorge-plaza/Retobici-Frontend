package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.BikeRemoteDataSource
import es.uva.retobici.frontend.domain.model.Bike
import javax.inject.Inject

class UnlockBikeUseCase @Inject constructor(
    private val remoteDataSource: BikeRemoteDataSource,
){

    suspend operator fun invoke(bike: Int): Bike{
        return remoteDataSource.unlockBike(bike)
    }
}