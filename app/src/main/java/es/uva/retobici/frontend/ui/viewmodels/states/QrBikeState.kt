package es.uva.retobici.frontend.ui.viewmodels.states

import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Stop

sealed class QrBikeState{
    object NoScanned: QrBikeState()
    data class QrScanned(val bike: Bike): QrBikeState()
    data class QrScannedEnd(val stop: Stop): QrBikeState()
}
