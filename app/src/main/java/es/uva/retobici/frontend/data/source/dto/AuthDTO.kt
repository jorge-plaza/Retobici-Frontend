package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.User

data class AuthDTO(
    @SerializedName("user") val user: UserDTO,
    @SerializedName("token") val token: String,
)
fun AuthDTO.toUserAuthenticated(): User{
    return User(user.id,user.email,token,user.points)
}
