package es.uva.retobici.frontend.ui.slideshow

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.uva.retobici.frontend.data.models.Stop
import es.uva.retobici.frontend.data.models.StopProvider
import es.uva.retobici.frontend.domain.GetStopsUseCase
import kotlinx.coroutines.launch

class SlideshowViewModel : ViewModel() {

    val stops = MutableLiveData<List<Stop>>()

    var getStopsUseCase = GetStopsUseCase()

    /** When the viewModel is created in the Fragment the All the Stops are loaded*/
    fun onCreate(){
        viewModelScope.launch {
            val result:List<Stop> = getStopsUseCase()
            stops.postValue(result)
        }
    }

    fun getStops(){
        //stops.postValue(StopProvider.getStops())
    }

    /*
    private val _text = MutableLiveData<String>().apply {
        value = "This is slideshow Fragment"
    }
    val text: LiveData<String> = _text*/
}