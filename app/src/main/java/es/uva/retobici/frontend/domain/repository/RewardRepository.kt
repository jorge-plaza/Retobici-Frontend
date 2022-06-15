package es.uva.retobici.frontend.domain.repository

import es.uva.retobici.frontend.domain.model.Reward

interface RewardRepository {
    suspend fun getAllRewards():List<Reward>
}