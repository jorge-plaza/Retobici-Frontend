package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.BikeDTO
import es.uva.retobici.frontend.data.source.dto.RouteDTO
import retrofit2.Response
import retrofit2.http.*

interface RouteAPI {
    @FormUrlEncoded
    @PUT("/retobici/routes/")
    suspend fun postStartRoute(
        @Field("id_user") id_user: Int,
        @Field("id_bike") id_bike: Int,
    ): Response<RouteDTO>

    @POST("/retobici/routes/{route}/finish")
    suspend fun postFinishRoute(
        @Path("route") route: Int
    ): Response<RouteDTO>
}