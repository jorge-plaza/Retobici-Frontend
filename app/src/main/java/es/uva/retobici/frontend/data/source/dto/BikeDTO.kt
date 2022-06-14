package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.ElectricBike
import es.uva.retobici.frontend.domain.model.PedalBike

data class BikeDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("unlocked") val unlocked: Boolean,
    @SerializedName("battery") val battery: Float?,
)
fun BikeDTO.toBikeModel(): Bike {
    return if (battery!=null) ElectricBike(id,unlocked,battery) else PedalBike(id,unlocked)
}
