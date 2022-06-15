package es.uva.retobici.frontend.ui.adapter

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.navigation.examples.databinding.ItemRewardBinding
import es.uva.retobici.frontend.domain.model.Reward

class RewardViewHolder(
    view: View
): RecyclerView.ViewHolder(view) {

    val binding = ItemRewardBinding.bind(view)

    fun render(reward: Reward){
        binding.rewardTitle.text = reward.title
        binding.rewardPoints.text = reward.points.toString()
        binding.rewardDate.text = "Fecha de fin: 12/11/1997"

        binding.itemRewardButtom.setOnClickListener {
            Toast.makeText(
                binding.itemRewardButtom.context,
                reward.title,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
