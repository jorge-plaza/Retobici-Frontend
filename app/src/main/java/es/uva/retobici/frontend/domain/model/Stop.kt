package es.uva.retobici.frontend.domain.model

import com.google.gson.JsonElement
import com.google.gson.JsonParser

data class Stop (
    val id: Int,
    val lng: Double,
    val lat: Double,
    val address: String,
    val totalSpaces: Int,
    val bikes: List<Bike>,
    val reservedPedalBikes: Int,
    val reservedElectricBikes: Int,
){
    val location: com.mapbox.geojson.Point = com.mapbox.geojson.Point.fromLngLat(lng,lat)
    private fun getCountPedalBike(): Int{
        return bikes.filterIsInstance<PedalBike>().size-reservedPedalBikes
    }
    private fun getCountElectricBike(): Int{
        return bikes.filterIsInstance<ElectricBike>().size-reservedElectricBikes
    }
    private fun getCountBikeStop(): Int{
        return totalSpaces-bikes.size-getTotalReservations()
    }
    private fun getTotalReservations(): Int{
        return reservedPedalBikes+reservedElectricBikes
    }

    fun toJson(): JsonElement{
        val json = """
            {
                id: "$id",
                address: "$address",
                count_bike_pedal: "${getCountPedalBike()}",
                count_bike_electric: "${getCountElectricBike()}",
                count_bike_stop: "${getCountBikeStop()}"
            }
        """.trimIndent()
        return JsonParser.parseString(json)
    }

    fun getTotalBikeCount(): Int {
        return bikes.size - getTotalReservations()
    }

    override fun equals(other: Any?): Boolean {
        //Todo check this
        val otherCast = other as Stop
        if (this.hashCode() != otherCast.hashCode()) return false
        return this.id == otherCast.id
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + lng.hashCode()
        result = 31 * result + lat.hashCode()
        result = 31 * result + address.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }
}