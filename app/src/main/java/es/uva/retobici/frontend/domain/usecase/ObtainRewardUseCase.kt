package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.RewardRemoteDataSource
import es.uva.retobici.frontend.domain.model.Reward
import javax.inject.Inject

class ObtainRewardUseCase @Inject constructor(
    private val remoteDataSource: RewardRemoteDataSource
){
    suspend operator fun invoke(reward: Reward): Reward{
        return remoteDataSource.obtainReward(reward)
    }
}
