package es.uva.retobici.frontend.data.repositories

import android.util.Log
import es.uva.retobici.frontend.data.source.api.RouteAPI
import es.uva.retobici.frontend.data.source.dto.toBikeModel
import es.uva.retobici.frontend.data.source.dto.toRouteModel
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.domain.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RouteRemoteDataSource @Inject constructor(
    private val api:RouteAPI
): RouteRepository {
    override suspend fun startRoute(bike: Bike): Route {
        return withContext(Dispatchers.IO){
            val response = api.postStartRoute(1, bike.id)
            response.body()!!.toRouteModel()
        }
    }
    override suspend fun finishRoute(route: Route): Route {
        return withContext(Dispatchers.IO){
            val response = api.postFinishRoute(route.id)
            // ?: if response is null empty list
            //response.body()?.map { it.toStopModel() } ?: emptyList()
            //TODO check the nullable response
            response.body()!!.toRouteModel()
        }
    }
}