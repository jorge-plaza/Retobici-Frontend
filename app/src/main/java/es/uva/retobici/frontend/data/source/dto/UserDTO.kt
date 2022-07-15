package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName

data class UserDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("points") val points: Int,
)
