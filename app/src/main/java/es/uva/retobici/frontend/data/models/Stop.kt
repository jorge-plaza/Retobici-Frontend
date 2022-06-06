package es.uva.retobici.frontend.data.models

import com.google.gson.annotations.SerializedName

data class Stop (
    @SerializedName("id") val id: Int,
    @SerializedName("lng") val lng: Double,
    @SerializedName("lat") val lat: Double,
    @SerializedName("address") val address: String,
){
    val location: com.mapbox.geojson.Point = com.mapbox.geojson.Point.fromLngLat(lng,lat)
}