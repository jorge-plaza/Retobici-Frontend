package es.uva.retobici.frontend.data.source.dto

import android.util.Log
import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.Route

data class RouteDTO (
    @SerializedName("id") val id: Int,
    @SerializedName("id_user") val id_user: Int,
    @SerializedName("bike") val bike: Int,
    @SerializedName("initial_stop") val initial_stop: Int?,
    @SerializedName("final_stop") val final_stop: Int?,
    @SerializedName("distance") val distance: Int?,
    @SerializedName("points") val points: Int?,
    @SerializedName("duration") val duration: Int?,
)
fun RouteDTO.toRouteModel(): Route {
    Log.d("hola", initial_stop.toString())
    return Route(id,id_user,bike,initial_stop,final_stop,distance,points,duration)
}