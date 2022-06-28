package es.uva.retobici.frontend.data.repositories

import android.util.Log
import es.uva.retobici.frontend.data.source.api.RouteAPI
import es.uva.retobici.frontend.data.source.dto.toRouteModel
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.domain.repository.RouteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RouteRemoteDataSource @Inject constructor(
    private val api:RouteAPI,
    private val userPreferences: UserPreferences
): RouteRepository {
    override suspend fun startRoute(bike: Bike): Route {
        return withContext(Dispatchers.IO){
            val token = userPreferences.authToken.first()
            val response = api.postStartRoute("Bearer $token",bike.bike_id)
            response.body()!!.toRouteModel()
        }
    }
    override suspend fun finishRoute(route: Route): Route {
        return withContext(Dispatchers.IO){
            val token = userPreferences.authToken.first()
            Log.d("calor", route.toString())
            val response = api.postFinishRoute("Bearer $token", route)
            Log.d("calor", response.toString())
            response.body()!!.toRouteModel()
        }
    }
}