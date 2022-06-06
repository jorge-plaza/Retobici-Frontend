package es.uva.retobici.frontend.data.repositories

import es.uva.retobici.frontend.data.models.Stop
import es.uva.retobici.frontend.data.models.StopProvider
import es.uva.retobici.frontend.data.network.StopService

class StopRepository {

    private val api = StopService()

    suspend fun getAllStops():List<Stop>{
        val response:List<Stop> = api.getStops()
        //El stop provider actua como pequeña base de datos
        StopProvider.stops = response
        return response
    }
}