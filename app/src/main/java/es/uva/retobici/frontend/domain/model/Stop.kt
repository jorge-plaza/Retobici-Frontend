package es.uva.retobici.frontend.domain.model

import com.google.gson.annotations.SerializedName

data class Stop (
    val id: Int,
    val lng: Double,
    val lat: Double,
    val address: String,
){
    val location: com.mapbox.geojson.Point = com.mapbox.geojson.Point.fromLngLat(lng,lat)
}