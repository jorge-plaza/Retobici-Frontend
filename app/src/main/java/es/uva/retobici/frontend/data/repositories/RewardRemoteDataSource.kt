package es.uva.retobici.frontend.data.repositories

import android.util.Log
import es.uva.retobici.frontend.data.UserPreferences
import es.uva.retobici.frontend.data.source.api.RewardAPI
import es.uva.retobici.frontend.data.source.dto.RewardDTO
import es.uva.retobici.frontend.data.source.dto.toRewardModel
import es.uva.retobici.frontend.domain.model.Reward
import es.uva.retobici.frontend.domain.repository.RewardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class RewardRemoteDataSource @Inject constructor(
    private val api: RewardAPI,
    private val userPreferences: UserPreferences
): RewardRepository {

    override suspend fun getAllRewards(): List<Reward> {
        return withContext(Dispatchers.IO){
            val response = api.getAllRewards()
            response.body()?.map { it.toRewardModel() } ?: emptyList()
        }
    }

    override suspend fun getUserRewards(): List<Reward> {
        return withContext(Dispatchers.IO){
            val authToken: String? = userPreferences.authToken.first()
            val response = api.getUserRewards("Bearer $authToken")
            response.body()?.map { it.toRewardModel() } ?: emptyList()
        }
    }

    override suspend fun getUserNotRedeemed(): List<Reward> {
        return withContext(Dispatchers.IO){
            val authToken: String? = userPreferences.authToken.first()
            val response = api.getUserNotRedeemed("Bearer $authToken")
            response.body()?.map { it.toRewardModel() } ?: emptyList()
        }
    }

    override suspend fun obtainReward(reward: Reward): Reward {
        return withContext(Dispatchers.IO){
            val authToken: String? = userPreferences.authToken.first()
            val response = api.postObtainReward("Bearer $authToken", reward.id)
            response.body()!!.toRewardModel()
        }
    }

}