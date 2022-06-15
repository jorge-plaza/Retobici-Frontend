package es.uva.retobici.frontend.data.repositories

import es.uva.retobici.frontend.data.source.api.RewardAPI
import es.uva.retobici.frontend.data.source.dto.toRewardModel
import es.uva.retobici.frontend.domain.model.Reward
import es.uva.retobici.frontend.domain.repository.RewardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

class RewardRemoteDataSource @Inject constructor(
    private val api: RewardAPI
): RewardRepository {

    override suspend fun getAllRewards(): List<Reward> {
        return withContext(Dispatchers.IO){
            val response = api.getAllRewards()

            response.body()?.map { it.toRewardModel() } ?: emptyList()
        }
    }

}