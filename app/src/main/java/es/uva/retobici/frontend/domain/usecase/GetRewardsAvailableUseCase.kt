package es.uva.retobici.frontend.domain.usecase

import es.uva.retobici.frontend.data.repositories.RewardRemoteDataSource
import es.uva.retobici.frontend.domain.model.Reward
import javax.inject.Inject

class GetRewardsAvailableUseCase @Inject constructor(
    private val remoteDataSource: RewardRemoteDataSource
) {
    suspend operator fun invoke(): List<Reward>{
        return remoteDataSource.getUserNotRedeemed()
    }
}
