package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.BikeRemoteDataSource
import es.uva.retobici.frontend.domain.model.Stop
import javax.inject.Inject

class ReserveBikeUseCase @Inject constructor(
    private val remoteDataSource: BikeRemoteDataSource
){
    suspend operator fun invoke(stop: Int):Stop{
        return remoteDataSource.reserveBike(stop)
    }
}
