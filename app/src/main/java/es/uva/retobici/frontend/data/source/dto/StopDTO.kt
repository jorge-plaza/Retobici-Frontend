package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.Stop

data class StopDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("lng") val lng: Double,
    @SerializedName("lat") val lat: Double,
    @SerializedName("address") val address: String,
)
fun StopDTO.toStopModel(): Stop{
    return Stop(
        id = this.id,
        lng = this.lng,
        lat = this.lat,
        address = this.address
    )
}
