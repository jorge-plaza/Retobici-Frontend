package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route

interface RouteRepository {
    suspend fun startRoute(bike: Bike):Route
    suspend fun finishRoute(route: Route):Route
}