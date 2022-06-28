package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.BikeDTO
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BikeAPI {

    @POST("stops/{bike}/unlock")
    suspend fun postUnlockBike(
        @Path("bike") bike: Int,
        @Header("Authorization") token: String
    ): Response<BikeDTO>

}