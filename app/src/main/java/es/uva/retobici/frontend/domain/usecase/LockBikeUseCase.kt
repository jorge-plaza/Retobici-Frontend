package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.StopRemoteDataSource
import es.uva.retobici.frontend.domain.model.Stop
import javax.inject.Inject

class LockBikeUseCase @Inject constructor(
    private val remoteDataSource: StopRemoteDataSource,
){

    suspend operator fun invoke(stop: Int): Stop {
        return remoteDataSource.lockBike(stop)
    }
}
