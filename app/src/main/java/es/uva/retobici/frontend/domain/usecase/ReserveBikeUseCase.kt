package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.BikeRemoteDataSource
import es.uva.retobici.frontend.data.repositories.StopRemoteDataSource
import es.uva.retobici.frontend.domain.model.Stop
import javax.inject.Inject

class ReserveBikeUseCase @Inject constructor(
    private val remoteDataSource: StopRemoteDataSource
){
    suspend operator fun invoke(stop: Int, type: String):Stop{
        return remoteDataSource.reserveBike(stop, type)
    }
}
