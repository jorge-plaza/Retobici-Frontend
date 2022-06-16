package es.uva.retobici.frontend.data.source.dto

import com.google.gson.annotations.SerializedName
import es.uva.retobici.frontend.domain.model.Reward

data class RewardDTO(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("points") val points: Int,
    @SerializedName("image") val image: String,
    @SerializedName("obtained") val obtained: Boolean,
)
fun RewardDTO.toRewardModel(): Reward{
    return Reward(id,title,description,points,image, obtained)
}
