package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.RouteRemoteDataSource
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route
import javax.inject.Inject

class StartRouteUseCase @Inject constructor(
    private val remoteDataSource: RouteRemoteDataSource
){
    suspend operator fun invoke(bike: Bike): Route {
        return remoteDataSource.startRoute(bike)
    }
}