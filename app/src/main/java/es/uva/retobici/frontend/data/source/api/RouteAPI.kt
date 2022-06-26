package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.RouteDTO
import es.uva.retobici.frontend.domain.model.Route
import retrofit2.Response
import retrofit2.http.*

interface RouteAPI {
    @PUT("routes/{bike}")
    suspend fun postStartRoute(
        @Header("Authorization") token: String,
        @Path("bike") bike: Int
    ): Response<RouteDTO>

    @POST("routes/finish")
    suspend fun postFinishRoute(
        @Header("Authorization") token: String,
        @Body route: Route
    ): Response<RouteDTO>
}