package es.uva.retobici.frontend.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.data.UserPreferences
import es.uva.retobici.frontend.domain.GetRewardsAvailableUseCase
import es.uva.retobici.frontend.domain.GetUserRewardsUseCase
import es.uva.retobici.frontend.domain.model.Reward
import es.uva.retobici.frontend.domain.usecase.GetRewardsUseCase
import es.uva.retobici.frontend.domain.usecase.ObtainRewardUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val getRewardsUseCase: GetRewardsUseCase,
    private val getUserRewardsUseCase: GetUserRewardsUseCase,
    private val getRewardsAvailableUseCase: GetRewardsAvailableUseCase,
    private val obtainRewardUseCase: ObtainRewardUseCase,
    private val userPreferences: UserPreferences,
) : ViewModel(){

    val rewards = MutableLiveData<List<Reward>>()
    val loading = MutableLiveData(false)

    /*
    init {
        viewModelScope.launch {
            val result:List<Reward> = getRewardsUseCase()
            rewards.postValue(result)
        }
    }*/

    fun getAllRewards(){
        viewModelScope.launch {
            loading.postValue(true)
            val result:List<Reward> = if (userPreferences.authToken.first()=="invalid" || userPreferences.authToken.first() == null){
                getRewardsUseCase()
            }else{
                getUserRewardsUseCase()
            }
            rewards.postValue(result)
            loading.postValue(false)
        }
    }

    fun getRewardsAvailable(){
        viewModelScope.launch {
            loading.postValue(true)
            val result:List<Reward> = getRewardsAvailableUseCase()
            rewards.postValue(result)
            loading.postValue(false)
        }
    }

    fun obtainReward(reward: Reward){
        viewModelScope.launch {
            loading.postValue(true)
            val result:Reward = obtainRewardUseCase(reward)
            val resultList:List<Reward> = getUserRewardsUseCase()
            val newPoints = userPreferences.points.first()?.minus(result.points)
            userPreferences.updatePoints(newPoints!!)
            rewards.postValue(resultList)
            loading.postValue(false)
        }
    }
}