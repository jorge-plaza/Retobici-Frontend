package es.uva.retobici.frontend.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import es.uva.retobici.frontend.domain.model.Stop

class HomeViewModel : ViewModel() {

    private val users: MutableLiveData<List<Stop>> by lazy {
        MutableLiveData<List<Stop>>().also {
            loadStops()
        }
    }

    fun getStops(): LiveData<List<Stop>> {
        return users
    }

    private fun loadStops() {
        // Do an asynchronous operation to fetch stops.
        val paradas: MutableList<Stop> = mutableListOf<Stop>(
            Stop(1,-4.731,41.653,"Plaza Mayor"),
            Stop(2,-4.726,41.652,"Teatro Calderon"),
            Stop(3,-4.729,41.647,"Plaza Poniente"),
            Stop(4,-4.725,41.648,"Plaza Mayor"),
        )

    }

}