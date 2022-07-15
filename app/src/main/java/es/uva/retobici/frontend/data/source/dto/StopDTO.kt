package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.Stop

data class StopDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("lng") val lng: Double,
    @SerializedName("lat") val lat: Double,
    @SerializedName("address") val address: String,
    @SerializedName("totalSpaces") val totalSpaces: Int,
    @SerializedName("bikes") val bikes: List<BikeDTO>,
    @SerializedName("reserved_pedal_bikes") val reservedPedalBikes: Int,
    @SerializedName("reserved_electric_bikes") val reservedElectricBikes: Int,
)
fun StopDTO.toStopModel(): Stop{
    return Stop(
        id = this.id,
        lng = this.lng,
        lat = this.lat,
        address = this.address,
        totalSpaces = this.totalSpaces,
        bikes = this.bikes.map { it.toBikeModel() },
        reservedPedalBikes = reservedPedalBikes,
        reservedElectricBikes = reservedElectricBikes
    )
}
