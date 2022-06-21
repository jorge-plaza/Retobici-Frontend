package es.uva.retobici.frontend.ui

import es.uva.retobici.frontend.domain.model.Bike

sealed class QrBikeState{
    object NoScanned: QrBikeState()
    data class QrScanned(val bike: Bike): QrBikeState()
}
