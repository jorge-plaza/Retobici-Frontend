package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.RouteDTO
import es.uva.retobici.frontend.domain.model.Route
import retrofit2.Response
import retrofit2.http.*

interface RouteAPI {
    @FormUrlEncoded
    @PUT("/routes/")
    suspend fun postStartRoute(
        @Field("id_user") id_user: Int,
        @Field("id_bike") id_bike: Int,
    ): Response<RouteDTO>

    @POST("/routes/finish")
    suspend fun postFinishRoute(
        @Body route: Route
    ): Response<RouteDTO>
}