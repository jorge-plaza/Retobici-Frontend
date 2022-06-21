package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route

data class RouteDTO (
    @SerializedName("id") val id: Int,
    @SerializedName("id_user") val id_user: Int,
    @SerializedName("bike") val bike: BikeDTO,
    @SerializedName("initial_stop") val initial_stop: Int?,
    @SerializedName("final_stop") val final_stop: Int?,
    @SerializedName("distance") val distance: Int?,
    @SerializedName("points") val points: Int?,
    @SerializedName("duration") val duration: Int?,
)
fun RouteDTO.toRouteModel(): Route {
    return Route(id,id_user,bike.toBikeModel(),initial_stop,final_stop,distance,points,duration)
}