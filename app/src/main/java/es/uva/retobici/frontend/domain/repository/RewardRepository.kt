package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Reward

interface RewardRepository {
    suspend fun getAllRewards():List<Reward>
    suspend fun getUserRewards(): List<Reward>
    suspend fun getUserNotRedeemed(): List<Reward>

    suspend fun obtainReward(reward: Reward): Reward
}