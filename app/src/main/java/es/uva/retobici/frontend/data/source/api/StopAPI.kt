package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.StopDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StopAPI {
    @GET("/stops")
    suspend fun getAllStops():Response<List<StopDTO>>

    @POST("/stops/{stop}/reserve/{type}")
    suspend fun postReserveBike(@Path("stop") stop: Int, @Path("type") type: String): Response<StopDTO>
}