package es.uva.retobici.frontend.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.domain.model.Reward
import es.uva.retobici.frontend.domain.usecase.GetRewardsUseCase
import es.uva.retobici.frontend.domain.usecase.ObtainRewardUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RewardsViewModel @Inject constructor(
    private val getRewardsUseCase: GetRewardsUseCase,
    private val obtainRewardUseCase: ObtainRewardUseCase,
) : ViewModel(){

    val rewards = MutableLiveData<List<Reward>>()

    init {
        viewModelScope.launch {
            val result:List<Reward> = getRewardsUseCase()
            rewards.postValue(result)
        }
    }

    fun obtainReward(reward: Reward){
        viewModelScope.launch {
            val result:Reward = obtainRewardUseCase(reward)
            //TODO replace value in list
            val resultList:List<Reward> = getRewardsUseCase()
            rewards.postValue(resultList)
        }
    }
}