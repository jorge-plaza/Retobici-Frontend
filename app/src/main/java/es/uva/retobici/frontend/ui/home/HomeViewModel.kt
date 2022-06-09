package es.uva.retobici.frontend.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.PedalBike
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.domain.usecase.GetStopsUseCase
import es.uva.retobici.frontend.domain.usecase.LockBikeUseCase
import es.uva.retobici.frontend.domain.usecase.UnlockBikeUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStopsUseCase: GetStopsUseCase,
    private val unlockBikeUseCase: UnlockBikeUseCase,
    private val lockBikeUseCase: LockBikeUseCase,
) : ViewModel() {

    val stops = MutableLiveData<List<Stop>>()

    /** When the viewModel is created in the Fragment the All the Stops are loaded*/
    init {
        viewModelScope.launch {
            val result:List<Stop> = getStopsUseCase()
            stops.postValue(result)
        }
        viewModelScope.launch {
            val result: Bike = unlockBikeUseCase(45)
            val locked: Bike = lockBikeUseCase(45, 1)
            //TODO update UI
            Log.d("bike", result.toString())
            Log.d("bike", locked.toString())
        }
    }

    //TODO check if this pattern is allowed, calling the view model from the view
}