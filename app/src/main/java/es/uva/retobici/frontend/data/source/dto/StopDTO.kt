package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.ElectricBike
import es.uva.retobici.frontend.domain.model.PedalBike
import es.uva.retobici.frontend.domain.model.Stop

data class StopDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("lng") val lng: Double,
    @SerializedName("lat") val lat: Double,
    @SerializedName("address") val address: String,
    @SerializedName("totalSpaces") val totalSpaces: Int,
    @SerializedName("bikes") val bikes: List<BikeDTO>
){
    data class BikeDTO(
        @SerializedName("id") val id: Int,
        @SerializedName("battery") val battery: Float?,
    ){
        fun toBikeModel(): Bike{
            return if (battery!=null) ElectricBike(id,battery) else PedalBike(id)
        }
    }
}
fun StopDTO.toStopModel(): Stop{
    return Stop(
        id = this.id,
        lng = this.lng,
        lat = this.lat,
        address = this.address,
        totalSpaces = this.totalSpaces,
        bikes = this.bikes.map { it.toBikeModel() }
    )
}
