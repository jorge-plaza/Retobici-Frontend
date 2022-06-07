package es.uva.retobici.frontend.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.domain.usecase.GetStopsUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getStopsUseCase: GetStopsUseCase
) : ViewModel() {

    val stops = MutableLiveData<List<Stop>>()

    /** When the viewModel is created in the Fragment the All the Stops are loaded*/
    init {
        viewModelScope.launch {
            val result:List<Stop> = getStopsUseCase()
            stops.postValue(result)
        }
    }

}