package es.uva.retobici.frontend.data.models

import es.uva.retobici.frontend.domain.model.Stop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StopProvider @Inject constructor(){
        var stops:List<Stop> = emptyList()

        /*
        private val oldstops = listOf<Stop>(
            Stop(1,-4.731,41.653,"Plaza Mayor"),
            Stop(2,-4.726,41.652,"Teatro Calderon"),
            Stop(3,-4.729,41.647,"Plaza Poniente"),
            Stop(4,-4.725,41.648,"Plaza Mayor"),
        )

        fun getStops():List<Stop>{
            return oldstops
        }
        */

}