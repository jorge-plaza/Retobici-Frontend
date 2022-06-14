package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.RouteRemoteDataSource
import es.uva.retobici.frontend.domain.model.Route
import javax.inject.Inject

class EndRouteUseCase @Inject constructor(
    private val remoteDataSource: RouteRemoteDataSource
){

    suspend operator fun invoke(route: Route, stop: Int, duration: Int): Route {
        route.final_stop = stop
        route.duration = duration
        return remoteDataSource.finishRoute(route)
    }
}