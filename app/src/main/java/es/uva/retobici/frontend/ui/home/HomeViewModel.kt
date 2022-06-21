package es.uva.retobici.frontend.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.core.Event
import es.uva.retobici.frontend.core.Timer
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.domain.usecase.*
import es.uva.retobici.frontend.ui.QrBikeState
import es.uva.retobici.frontend.ui.RouteState
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStopsUseCase: GetStopsUseCase,
    private val unlockBikeUseCase: UnlockBikeUseCase,
    private val startRouteUseCase: StartRouteUseCase,
    private val endRouteUseCase: EndRouteUseCase,
    private val reserveBikeUseCase: ReserveBikeUseCase,
) : ViewModel() {

    val stops = MutableLiveData<MutableList<Stop>>()
    val bikeAvailable = MutableLiveData<QrBikeState>()
    val route = MutableLiveData<RouteState>()

    val reserved = MutableLiveData<Boolean>()
    val loading = MutableLiveData(false)

    private val _bikeReserved = MutableLiveData<Event<Boolean>>()
    val bikeReserved : LiveData<Event<Boolean>> get() = _bikeReserved

    /** When the viewModel is created in the Fragment the All the Stops are loaded*/
    init {
        loading.value = true
        viewModelScope.launch {
            val result:List<Stop> = getStopsUseCase()
            val mut = result.toMutableList()
            stops.postValue(mut)
            loading.postValue(false)
        }
    }

    fun clearRoute(){
        route.postValue(RouteState.NoRoute)
        bikeAvailable.postValue(QrBikeState.NoScanned)
        seconds.postValue(0)
    }

    fun getBike(stop: Int){
        loading.postValue(true)
        viewModelScope.launch {
            //TODO change unlock use case to get available bike
            val availableBike: Bike = unlockBikeUseCase(stop)
            bikeAvailable.postValue(QrBikeState.QrScanned(availableBike))
            loading.postValue(false)
        }
    }

    private lateinit var timer: Timer
    var seconds = MutableLiveData(0)

    private fun startRoute() {
        loading.postValue(true)
        timer = Timer()
        seconds = timer.seconds
        viewModelScope.launch {
            with(bikeAvailable.value as QrBikeState.QrScanned){
                val result: Route = startRouteUseCase(this.bike)
                route.postValue(RouteState.ActiveRoute(result))
                reserved.postValue(false)
                loading.postValue(false)
            }
        }
    }

    private fun finishRoute(stop: Int){
        loading.postValue(true)
        timer.cancelTimer()
        viewModelScope.launch {
            var result: Route? = null
            with(route.value as RouteState.ActiveRoute){
                result= endRouteUseCase(this.route, stop, seconds.value!!)
            }
            route.postValue(RouteState.FinishedRoute(result!!))
            loading.postValue(false)
        }
    }

    fun reserveBike(stop: Int) {
        loading.value = true
        viewModelScope.launch {
            val result: Stop = reserveBikeUseCase(stop, "electric")
            val index = stops.value!!.indexOf(result)
            if (index!=-1) stops.value!![index] = result
            stops.postValue(stops.value)
            _bikeReserved.value = Event(true)
            reserved.postValue(true)
            loading.postValue(false)
        }
    }

    fun reserveElectricBike() {

    }

    fun performRoute(stop: Int?) {
        when(route.value){
            null -> {startRoute()}
            is RouteState.NoRoute -> { startRoute() }
            is RouteState.ActiveRoute -> { finishRoute(stop!!) }
            is RouteState.FinishedRoute -> { clearRoute() }
        }
    }

}