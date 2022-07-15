package es.uva.retobici.frontend.ui.viewmodels.states

import es.uva.retobici.frontend.domain.model.Stop

sealed class ReservationState{
    object NoReserve: ReservationState()
    data class ActiveReservation(val stop: Stop): ReservationState()
}
