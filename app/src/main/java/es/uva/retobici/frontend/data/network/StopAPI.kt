package es.uva.retobici.frontend.data.network

import es.uva.retobici.frontend.data.models.Stop
import retrofit2.Response
import retrofit2.http.GET

interface StopAPI {
    @GET("/retobici/stops")
    suspend fun getAllStops():Response<List<Stop>>
}