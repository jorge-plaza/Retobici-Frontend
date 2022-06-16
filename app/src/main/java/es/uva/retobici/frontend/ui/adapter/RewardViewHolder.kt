package es.uva.retobici.frontend.ui.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.navigation.examples.databinding.ItemRewardBinding
import es.uva.retobici.frontend.domain.model.Reward

class RewardViewHolder(
    view: View
): RecyclerView.ViewHolder(view) {

    //late init inteface implementd on the fragment

    val binding = ItemRewardBinding.bind(view)

    fun render(reward: Reward, onClickListener: (Reward) -> Unit){
        binding.rewardTitle.text = reward.title
        binding.rewardPoints.text = reward.points.toString()
        binding.rewardDate.text = "Fecha de fin: 12/11/1997"

        //If the reward have been obtained can not be obtained again
        if (reward.obtained){
            binding.itemRewardButtom.isEnabled = false
            binding.textRewardObtained.visibility = View.VISIBLE
        }

        binding.itemRewardButtom.setOnClickListener { onClickListener(reward) }
    }
}
