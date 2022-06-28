package es.uva.retobici.frontend.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.core.Timer
import es.uva.retobici.frontend.data.repositories.UserPreferences
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.domain.usecase.*
import es.uva.retobici.frontend.ui.viewmodels.states.QrBikeState
import es.uva.retobici.frontend.ui.viewmodels.states.ReservationState
import es.uva.retobici.frontend.ui.viewmodels.states.RouteState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStopsUseCase: GetStopsUseCase,
    private val unlockBikeUseCase: UnlockBikeUseCase,
    private val startRouteUseCase: StartRouteUseCase,
    private val endRouteUseCase: EndRouteUseCase,
    private val reserveBikeUseCase: ReserveBikeUseCase,
    private val lockBikeUseCase: LockBikeUseCase,

    private val userPreferences: UserPreferences
) : ViewModel() {
    private val userEmail = MutableLiveData<String>()
    private val userPoints = MutableLiveData<Int>()

    val stops = MutableLiveData<MutableList<Stop>>()
    val bikeAvailable = MutableLiveData<QrBikeState>()
    val route = MutableLiveData<RouteState>(RouteState.NoRoute)
    val reserved = MutableLiveData<ReservationState>(ReservationState.NoReserve)

    val loading = MutableLiveData(false)
    /** When the viewModel is created in the Fragment the All the Stops are loaded*/
    init {
        loading.value = true
        viewModelScope.launch {
            val result:List<Stop> = getStopsUseCase()
            val mut = result.toMutableList()
            stops.postValue(mut)
            loading.postValue(false)
            userPreferences.email.collect{ userEmail.postValue(it) }
            userPreferences.points.collect{ userPoints.postValue(it) }
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
            val availableBike: Bike = unlockBikeUseCase(stop)
            bikeAvailable.postValue(QrBikeState.QrScanned(availableBike))
            loading.postValue(false)
        }
    }

    fun setBike(stop: Int){
        loading.postValue(true)
        viewModelScope.launch {
            val availableStop: Stop = lockBikeUseCase(stop)
            bikeAvailable.postValue(QrBikeState.QrScannedEnd(availableStop))
            loading.postValue(false)
        }
    }

    private lateinit var timer: Timer
    var seconds = MutableLiveData(0)

    private fun startRoute() {
        loading.postValue(true)
        timer = Timer()
        seconds = timer.seconds
        reserved.postValue(ReservationState.NoReserve)
        viewModelScope.launch {
            with(bikeAvailable.value as QrBikeState.QrScanned){
                val result: Route = startRouteUseCase(this.bike)
                route.postValue(RouteState.ActiveRoute(result))
                loading.postValue(false)
            }
            val result:List<Stop> = getStopsUseCase()
            val mut = result.toMutableList()
            stops.postValue(mut)
        }
    }

    private fun finishRoute(stop: Int){
        loading.postValue(true)
        timer.cancelTimer()
        viewModelScope.launch {
            var result: Route?
            with(route.value as RouteState.ActiveRoute){
                result= endRouteUseCase(this.route, stop, seconds.value!!)
            }
            route.postValue(RouteState.FinishedRoute(result!!))
            val newPoints = userPreferences.points.first()?.plus(result!!.points!!)
            userPreferences.updatePoints(newPoints!!)
            loading.postValue(false)
            val refreshStops:List<Stop> = getStopsUseCase()
            val mut = refreshStops.toMutableList()
            stops.postValue(mut)
        }
    }

    fun reserveBike(stop: Int) {
        loading.value = true
        viewModelScope.launch {
            val result: Stop = reserveBikeUseCase(stop, "pedal")
            val index = stops.value!!.indexOf(result)
            if (index!=-1) stops.value!![index] = result
            stops.postValue(stops.value)
            reserved.postValue(ReservationState.ActiveReservation(result))
            loading.postValue(false)
        }
    }

    fun reserveElectricBike(stop: Int) {
        loading.value = true
        viewModelScope.launch {
            val result: Stop = reserveBikeUseCase(stop, "electric")
            val index = stops.value!!.indexOf(result)
            if (index!=-1) stops.value!![index] = result
            stops.postValue(stops.value)
            reserved.postValue(ReservationState.ActiveReservation(result))
            loading.postValue(false)
        }
    }

    fun performRoute(stop: Int?) {
        when(route.value){
            is RouteState.NoRoute -> { startRoute() }
            is RouteState.ActiveRoute -> { finishRoute(stop!!) }
            is RouteState.FinishedRoute -> { clearRoute() }
            null -> { startRoute() }
        }
    }

}