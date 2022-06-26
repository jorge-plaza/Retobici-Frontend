package es.uva.retobici.frontend.ui

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.mapbox.navigation.examples.databinding.FragmentRewardsBinding
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.MasterActivity
import es.uva.retobici.frontend.data.UserPreferences
import es.uva.retobici.frontend.domain.model.Reward
import es.uva.retobici.frontend.ui.adapter.RewardAdapter
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class RewardsFragment : Fragment() {

    companion object {
        fun newInstance() = RewardsFragment()
    }

    private var _binding: FragmentRewardsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RewardsViewModel by activityViewModels()
    private lateinit var masterActivity: MasterActivity

    @Inject lateinit var userPreferences: UserPreferences
    private var authenticated = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardsBinding.inflate(inflater, container, false)
        viewModel.getAllRewards()
        masterActivity = activity as MasterActivity

        userPreferences.authToken.asLiveData().observe(viewLifecycleOwner){ authToken ->
            if (authToken==null || authToken=="invalid" || authToken.isEmpty()){
                authenticated = false
                binding.chipsGroup.visibility = View.GONE
                binding.userPoints.visibility = View.GONE
                binding.userPointsLabel.visibility = View.GONE
            }else{
                authenticated = true
                binding.chipsGroup.visibility = View.VISIBLE
                binding.userPoints.visibility = View.VISIBLE
                binding.userPointsLabel.visibility = View.VISIBLE
            }
        }

        userPreferences.points.asLiveData().observe(viewLifecycleOwner){ points ->
            if (points != null && points != -1) binding.userPoints.text = points.toString()
        }

        viewModel.rewards.observe(viewLifecycleOwner) { rewards -> initRecyclerView(rewards) }
        viewModel.loading.observe(viewLifecycleOwner){ masterActivity.loading(it) }

        binding.availableChip.setOnClickListener { viewModel.getRewardsAvailable() }
        binding.redeemedChip.setOnClickListener { viewModel.getAllRewards() }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(rewards: List<Reward>) {
        val recyclerView = binding.recyclerRewards
        recyclerView.layoutManager = LinearLayoutManager(this.requireContext())
        recyclerView.adapter = RewardAdapter(rewards, authenticated, binding.userPoints.text.toString().toInt()) { reward -> onClickObtain(reward) }
    }

    private fun onClickObtain(reward: Reward) {
        masterActivity.loading(true)
        binding.redeemedChip.isChecked = true
        binding.availableChip.isChecked = false
        viewModel.obtainReward(reward)
    }
}