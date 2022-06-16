package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.BikeDTO
import es.uva.retobici.frontend.data.source.dto.RewardDTO
import es.uva.retobici.frontend.domain.model.Reward
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RewardAPI {
    @GET("/retobici/rewards")
    suspend fun getAllRewards(): Response<List<RewardDTO>>

    @POST("/retobici/rewards/{reward}/obtain/")
    suspend fun postObtainReward(@Path("reward") reward: Int): Response<RewardDTO>
}