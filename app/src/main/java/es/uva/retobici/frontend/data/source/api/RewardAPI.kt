package es.uva.retobici.frontend.data.source.api

import es.uva.retobici.frontend.data.source.dto.RewardDTO
import retrofit2.Response
import retrofit2.http.GET

interface RewardAPI {
    @GET("/retobici/rewards")
    suspend fun getAllRewards(): Response<List<RewardDTO>>
}