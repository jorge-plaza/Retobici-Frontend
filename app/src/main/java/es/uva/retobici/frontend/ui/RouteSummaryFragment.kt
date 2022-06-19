package es.uva.retobici.frontend.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.mapbox.navigation.examples.databinding.FragmentRouteSummaryBinding
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.domain.model.Route
import es.uva.retobici.frontend.ui.home.HomeViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [RouteSummaryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class RouteSummaryFragment : Fragment() {
    private var _binding: FragmentRouteSummaryBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRouteSummaryBinding.inflate(inflater, container, false)
        homeViewModel.route.observe(this.viewLifecycleOwner){ route ->
            if (route == null){
                view?.findNavController()?.navigate(com.mapbox.navigation.examples.R.id.action_routeSummaryFragment_to_nav_home)
            }
            else showRouteInfo(route)
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
        val duration = route.duration.toString()
        val points = route.points.toString()
        binding.routeDistance.text = "$distance metros"
        binding.routeDuration.text = "$duration minutos"
        binding.routePoints.text = "$points puntos"
    }
}