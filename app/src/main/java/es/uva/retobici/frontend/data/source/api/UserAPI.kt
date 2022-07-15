package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.AuthDTO
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

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