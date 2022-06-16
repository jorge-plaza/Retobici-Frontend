package es.uva.retobici.frontend.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.navigation.examples.databinding.FragmentRewardsBinding
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.MasterActivity
import es.uva.retobici.frontend.domain.model.Reward
import es.uva.retobici.frontend.ui.adapter.RewardAdapter

@AndroidEntryPoint
class RewardsFragment : Fragment() {

    companion object {
        fun newInstance() = RewardsFragment()
    }

    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RewardsViewModel by activityViewModels()
    private lateinit var masterActivity: MasterActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        masterActivity = activity as MasterActivity
        viewModel.rewards.observe(this.viewLifecycleOwner) { rewards ->
            initRecyclerView(rewards)
            masterActivity.loading(false)
        }
        masterActivity.loading(true)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(rewards: List<Reward>) {
        val recyclerView = binding.recyclerRewards
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerView.adapter = RewardAdapter(rewards) { reward -> onClickObtain(reward) }
    }

    private fun onClickObtain(reward: Reward) {
        masterActivity.loading(true)
        viewModel.obtainReward(reward)
    }
}