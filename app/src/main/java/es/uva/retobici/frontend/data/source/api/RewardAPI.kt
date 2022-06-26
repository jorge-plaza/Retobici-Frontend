package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.BikeDTO
import es.uva.retobici.frontend.data.source.dto.RewardDTO
import es.uva.retobici.frontend.domain.model.Reward
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface RewardAPI {
    @GET("rewards")
    suspend fun getAllRewards(): Response<List<RewardDTO>>

    @GET("rewards/redeemed")
    suspend fun getUserRewards(@Header("Authorization") token: String): Response<List<RewardDTO>>

    @GET("rewards/not/redeemed")
    suspend fun getUserNotRedeemed(@Header("Authorization") token: String): Response<List<RewardDTO>>

    @POST("rewards/{reward}")
    suspend fun postObtainReward(
        @Header("Authorization") token: String,
        @Path("reward") reward: Int
    ): Response<RewardDTO>
}