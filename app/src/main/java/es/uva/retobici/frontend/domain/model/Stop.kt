package es.uva.retobici.frontend.domain.model

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName
import kotlin.reflect.typeOf

data class Stop (
    val id: Int,
    val lng: Double,
    val lat: Double,
    val address: String,
    val totalSpaces: Int,
    val bikes: List<Bike>,
){
    val location: com.mapbox.geojson.Point = com.mapbox.geojson.Point.fromLngLat(lng,lat)
    fun getCountPedalBike(): Int{
        return bikes.filterIsInstance<PedalBike>().size
    }
    fun getCountElectricBike(): Int{
        return bikes.filterIsInstance<ElectricBike>().size
    }
    fun getCountBikeStop(): Int{
        return totalSpaces-bikes.size
    }

    fun toJson(): JsonElement{
        val json = """
            {
                address: "$address",
                count_bike_pedal: "${getCountPedalBike()}",
                count_bike_electric: "${getCountElectricBike()}",
                count_bike_stop: "${getCountBikeStop()}"
            }
        """.trimIndent()
        return JsonParser.parseString(json)
    }
}