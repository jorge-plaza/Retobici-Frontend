package es.uva.retobici.frontend.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.navigation.examples.R
import es.uva.retobici.frontend.domain.model.Reward

class RewardAdapter(
    private val rewardsList: List<Reward>,
    private val authenticated: Boolean,
    private val points: Int,
    private val onClickListener: (Reward) -> Unit,
) : RecyclerView.Adapter<RewardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RewardViewHolder(layoutInflater.inflate(R.layout.item_reward, parent, false))
    }

    override fun onBindViewHolder(holder: RewardViewHolder, position: Int) {
        val item = rewardsList[position]
        holder.render(item, authenticated, points, onClickListener)
    }

    override fun getItemCount(): Int = rewardsList.size
}
