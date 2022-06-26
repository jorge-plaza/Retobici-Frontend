package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.AuthDTO
import es.uva.retobici.frontend.data.source.dto.RouteDTO
import es.uva.retobici.frontend.data.source.dto.UserDTO
import retrofit2.Response
import retrofit2.http.*

interface UserAPI {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Response<AuthDTO>

    @POST("logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Boolean>
}