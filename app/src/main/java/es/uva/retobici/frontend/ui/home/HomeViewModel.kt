package es.uva.retobici.frontend.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.domain.usecase.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStopsUseCase: GetStopsUseCase,
    private val unlockBikeUseCase: UnlockBikeUseCase,
    private val startRouteUseCase: StartRouteUseCase,
    private val endRouteUseCase: EndRouteUseCase,
    private val reserveBikeUseCase: ReserveBikeUseCase,
) : ViewModel() {

    val stops = MutableLiveData<List<Stop>>()
    val unlockedBike = MutableLiveData<Bike>()
    val route = MutableLiveData<Route>()
    val reserved = MutableLiveData<Boolean>()

    /** When the viewModel is created in the Fragment the All the Stops are loaded*/
    init {
        viewModelScope.launch {
            val result:List<Stop> = getStopsUseCase()
            stops.postValue(result)
        }
    }

    fun clearRoute(){
        route.postValue(null)
    }

    fun unlockBike(bike: Int){
        viewModelScope.launch {
            val result: Bike = unlockBikeUseCase(bike)
            unlockedBike.postValue(result)
        }
    }

    fun startRoute() {
        viewModelScope.launch {
            val result: Route = startRouteUseCase(unlockedBike.value!!)
            route.postValue(result)
        }
    }

    fun finishRoute(stop: Int, duration: Int){
        viewModelScope.launch {
            val result: Route = endRouteUseCase(route.value!!, stop, duration)
            route.postValue(result)
        }
    }

    fun reserveBike(stop: Stop) {
        viewModelScope.launch {
            val result: Boolean = reserveBikeUseCase(stop)
            reserved.postValue(result)
            //TODO decidir como mostrar una bici reservada
        }
    }

    fun reserveElectricBike() {

    }

}