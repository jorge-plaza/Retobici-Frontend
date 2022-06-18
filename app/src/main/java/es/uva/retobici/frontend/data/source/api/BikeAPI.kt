package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.BikeDTO
import es.uva.retobici.frontend.data.source.dto.StopDTO
import es.uva.retobici.frontend.domain.model.Bike
import retrofit2.Response
import retrofit2.http.*

interface BikeAPI {
    @POST("/retobici/bikes/unlock/{bike}/")
    suspend fun postUnlockBike(@Path("bike") bike: Int): Response<BikeDTO>

    @POST("/retobici/bikes/reserve/{stop}/reserve")
    suspend fun postReserveBike(@Path("stop") stop: Int): Response<StopDTO>
}