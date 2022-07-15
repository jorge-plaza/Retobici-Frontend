package es.uva.retobici.frontend.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.mapbox.navigation.examples.databinding.FragmentRouteSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.MasterActivity
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.ui.viewmodels.HomeViewModel
import es.uva.retobici.frontend.ui.viewmodels.states.RouteState
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
class RouteSummaryFragment : Fragment() {
    private var _binding: FragmentRouteSummaryBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var masterActivity: MasterActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteSummaryBinding.inflate(inflater, container, false)
        masterActivity = activity as MasterActivity
        homeViewModel.loading.observe(viewLifecycleOwner) { masterActivity.loading(it) }

        homeViewModel.route.observe(this.viewLifecycleOwner) { route ->
            when (route) {
                is RouteState.NoRoute -> {
                    view?.findNavController()
                        ?.navigate(com.mapbox.navigation.examples.R.id.action_routeSummaryFragment_to_nav_home)
                }
                is RouteState.ActiveRoute -> {}
                is RouteState.FinishedRoute -> showRouteInfo(route.route)
            }
        }

        binding.finishRouteButton.setOnClickListener {
            homeViewModel.clearRoute()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRouteInfo(route: Route) {
        val distance = route.distance.toString()
        val time = experimentalConversion(route.duration)
        val points = route.points.toString()
        binding.routeDistance.text = "$distance metros"
        binding.routeDuration.text = time
        binding.routePoints.text = "$points puntos"
    }

    @OptIn(ExperimentalTime::class)
    fun experimentalConversion(seconds: Int?): String {
        val converted = seconds?.let {
            Duration.convert(
                it.toDouble(),
                DurationUnit.SECONDS,
                DurationUnit.MINUTES
            ).minutes
        }
        return converted.toString()
    }
}