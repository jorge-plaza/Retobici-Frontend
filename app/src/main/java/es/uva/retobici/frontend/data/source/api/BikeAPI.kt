package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.BikeDTO
import es.uva.retobici.frontend.data.source.dto.StopDTO
import es.uva.retobici.frontend.domain.model.Bike
import retrofit2.Response
import retrofit2.http.*

interface BikeAPI {

    @POST("stops/{bike}/unlock")
    suspend fun postUnlockBike(
        @Path("bike") bike: Int,
        @Header("Authorization") token: String
    ): Response<BikeDTO>

}