package es.uva.retobici.frontend.ui

import es.uva.retobici.frontend.domain.model.Route

sealed class RouteState{
    object NoRoute: RouteState()
    data class ActiveRoute(val route: Route): RouteState()
    data class FinishedRoute(val route: Route): RouteState()
}
