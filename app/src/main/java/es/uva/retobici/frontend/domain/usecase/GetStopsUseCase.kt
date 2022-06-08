package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.data.repositories.StopRemoteDataSource
import javax.inject.Inject

class GetStopsUseCase @Inject constructor(
    private val remoteDataSource: StopRemoteDataSource
){

    suspend operator fun invoke(): List<Stop> {
        return remoteDataSource.getAllStops()
    }
}