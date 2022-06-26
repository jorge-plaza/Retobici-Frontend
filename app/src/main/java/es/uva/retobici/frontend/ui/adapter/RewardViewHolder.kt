package es.uva.retobici.frontend.ui.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mapbox.navigation.examples.databinding.ItemRewardBinding
import es.uva.retobici.frontend.domain.model.Reward

class RewardViewHolder(
    view: View
): RecyclerView.ViewHolder(view) {

    val binding = ItemRewardBinding.bind(view)

    fun render(reward: Reward, authenticated: Boolean, userPoints: Int,onClickListener: (Reward) -> Unit){
        binding.rewardTitle.text = reward.title
        binding.rewardPoints.text = "Puntos necesarios ${reward.points}"
        binding.rewardDate.text = "Válido hasta: 12/11/2022"
        Glide.with(binding.imageReward.context).load(reward.image).into(binding.imageReward)
        binding.rewardInfo.visibility = View.VISIBLE
        //If the reward have been obtained can not be obtained again
        if (!authenticated){
            binding.itemRewardButtom.isEnabled = false
            binding.textRewardObtained.text = "Inicia sesión para obtener recompensas"
            binding.textRewardObtained.visibility = View.VISIBLE
            binding.imageQr.visibility = View.INVISIBLE
        }else{
            if (reward.obtained || userPoints<reward.points){
                binding.itemRewardButtom.isEnabled = false
                binding.textRewardObtained.visibility = View.VISIBLE
                binding.textRewardObtained.text = "Recompensa ya obtenida"
            }else{
                binding.itemRewardButtom.isEnabled = true
                binding.textRewardObtained.visibility = View.INVISIBLE
                binding.imageQr.visibility = View.INVISIBLE
                binding.textRewardObtained.text = "Recompensa ya obtenida"
            }
        }
        binding.itemRewardButtom.setOnClickListener { onClickListener(reward) }
    }
}
