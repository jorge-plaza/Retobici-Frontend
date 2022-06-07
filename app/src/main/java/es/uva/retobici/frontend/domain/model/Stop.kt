package es.uva.retobici.frontend.domain.model

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.annotations.SerializedName

data class Stop (
    val id: Int,
    val lng: Double,
    val lat: Double,
    val address: String,
){
    val location: com.mapbox.geojson.Point = com.mapbox.geojson.Point.fromLngLat(lng,lat)

    fun toJson(): JsonElement{
        val json = """
            {
                address: "$address",
                count_bike_pedal: 3,
                count_bike_electric: 2,
                count_bike_stop: 9
            }
        """.trimIndent()
        return JsonParser.parseString(json)
    }
}