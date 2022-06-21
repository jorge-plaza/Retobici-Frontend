package es.uva.retobici.frontend.data.source.dto

import android.util.Log
import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.User

data class UserDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("points") val points: Int,
)
