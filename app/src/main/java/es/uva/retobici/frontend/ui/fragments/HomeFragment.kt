package es.uva.retobici.frontend.ui.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.annotation.AnnotationPlugin
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.navigation.R
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.examples.databinding.AnnotationViewNumberStopBinding
import com.mapbox.navigation.examples.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.MasterActivity
import es.uva.retobici.frontend.data.repositories.UserPreferences
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.ElectricBike
import es.uva.retobici.frontend.domain.model.Stop
import es.uva.retobici.frontend.ui.viewmodels.states.ReservationState
import es.uva.retobici.frontend.ui.viewmodels.states.RouteState
import es.uva.retobici.frontend.ui.viewmodels.HomeViewModel
import org.json.JSONObject
import javax.inject.Inject
import kotlin.time.Duration.Companion.convert
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private lateinit var masterActivity: MasterActivity

    private var _bottomSheetBehavior: BottomSheetBehavior<LinearLayout>? = null
    private var _bottomSheetBehaviorRoute: BottomSheetBehavior<LinearLayout>? = null
    private var _pointAnnotationManager: PointAnnotationManager? = null
    private var _annotationApi: AnnotationPlugin? = null

    private val bottomSheetBehavior get() = _bottomSheetBehavior!!
    private val bottomSheetBehaviorRoute get() = _bottomSheetBehaviorRoute!!
    private val pointAnnotationManager get() = _pointAnnotationManager!!
    private val annotationApi get() = _annotationApi!!

    @Inject lateinit var userPreferences: UserPreferences

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private var isReserved = false
    private var onRoute = false

    private val homeViewModel : HomeViewModel by activityViewModels()

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Debug tool used to play, pause and seek route progress events that can be used to produce mocked location updates along the route.
     */
    private val mapboxReplayer = MapboxReplayer()

    /**
     * Mapbox Maps entry point obtained from the [MapView].
     * You need to get a new reference to this object whenever the [MapView] is recreated.
     */
    private lateinit var mapboxMap: MapboxMap


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        masterActivity = activity as MasterActivity
        //Fused location to retrieve device location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        _bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContentStop.persistentBottomSheetStop)
        _bottomSheetBehaviorRoute = BottomSheetBehavior.from(binding.bottomSheetContentRoute.persistentBottomSheetRoute)

        userPreferences.authToken.asLiveData().observe(viewLifecycleOwner){ authToken ->
            if (authToken==null || authToken=="invalid" || authToken.isEmpty()){
                binding.qrScan.visibility = View.INVISIBLE
                binding.bottomSheetContentStop.pedalBikeLayout.isClickable = false
                binding.bottomSheetContentStop.electricBikeLayout.isClickable = false
                binding.bottomSheetContentStop.reserveBikeButton.text = "Necesitas iniciar sesión para reservar"
                binding.bottomSheetContentStop.reserveBikeButton.isEnabled = false
            }else{
                binding.qrScan.visibility = View.VISIBLE
                binding.bottomSheetContentStop.pedalBikeLayout.isClickable = true
                binding.bottomSheetContentStop.electricBikeLayout.isClickable = true
                binding.bottomSheetContentStop.reserveBikeButton.text = "Seleccione para Reservar"
                binding.bottomSheetContentStop.reserveBikeButton.isEnabled = false
            }
        }

        homeViewModel.loading.observe(this.viewLifecycleOwner){ masterActivity.loading(it) }

        homeViewModel.stops.observe(this.viewLifecycleOwner) { stops -> addAnnotationToMap(stops) }

        homeViewModel.reserved.observe(viewLifecycleOwner){ reservationState ->
            when(reservationState){
                is ReservationState.NoReserve -> { isReserved = false }
                is ReservationState.ActiveReservation -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    showSnackBar("Tienes una bici reservada por 10 minutos")
                    isReserved = true
                }
            }
            requireActivity().invalidateOptionsMenu()
        }

        homeViewModel.route.observe(this.viewLifecycleOwner){ routeState ->
            when(routeState){
                is RouteState.NoRoute -> {
                    binding.bottomSheetContentRoute.persistentBottomSheetRoute.visibility = View.GONE
                    setOnRouteState(false)
                }
                is RouteState.ActiveRoute -> {
                    masterActivity.loading(true)
                    setRouteWithBike(routeState.route.bike)
                }
                is RouteState.FinishedRoute -> {}
            }
        }

        homeViewModel.seconds.observe(this.viewLifecycleOwner){ seconds ->
            binding.bottomSheetContentRoute.routeDurationTimer.text = experimentalConversion(seconds)
        }

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehaviorRoute.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    showSnackBar("Haz click en el icono de la bici verde en la parte superior para visualizar la ruta", Snackbar.LENGTH_INDEFINITE)
                }
            }
        })

        binding.qrScan.setOnClickListener { view ->
            //Load QR scan Fragment
            //TODO fix packages
            view.findNavController().navigate(com.mapbox.navigation.examples.R.id.action_nav_home_to_qr_scan)
        }

        binding.bottomSheetContentStop.pedalBikeLayout.setOnClickListener {
            //Reserve this type of bike
            val stopID = binding.bottomSheetContentStop.stopId
            val button = binding.bottomSheetContentStop.reserveBikeButton
            val countBikesAvailable = binding.bottomSheetContentStop.countPedalBike.text.toString().toInt()
            //Only if no reservation the button is accessible
            if (!isReserved){
                if (countBikesAvailable>0){
                    button.isEnabled = true
                    button.text = "Reservar Bici"
                    button.setOnClickListener {
                        homeViewModel.reserveBike(stopID.text.toString().toInt())
                    }
                }else{
                    button.isEnabled = false
                    button.text = "No hay bicis disponibles"
                }
            }
        }
        binding.bottomSheetContentStop.electricBikeLayout.setOnClickListener {
            //Reserve this type of bike
            val stopID = binding.bottomSheetContentStop.stopId
            val button = binding.bottomSheetContentStop.reserveBikeButton
            val countBikesAvailable = binding.bottomSheetContentStop.countElectricBike.text.toString().toInt()
            //Only if no reservation the button is accessible
            if (!isReserved){
                if (countBikesAvailable>0){
                    button.isEnabled = true
                    button.text = "Reservar Bici Eléctrica"
                    button.setOnClickListener {
                        homeViewModel.reserveElectricBike(stopID.text.toString().toInt())
                    }
                }else{
                    button.isEnabled = false
                    button.text = "No hay bicis disponibles"
                }
            }
        }

        binding.bottomSheetContentStop.scanQrOnStopButton.setOnClickListener {
            view?.findNavController()?.navigate(com.mapbox.navigation.examples.R.id.action_nav_home_to_qr_scan)
        }

        //Init annotation api for only use one instance
        _annotationApi = binding.mapView.annotations
        _pointAnnotationManager = annotationApi.createPointAnnotationManager()

        mapboxMap = binding.mapView.getMapboxMap()

        // load map style
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            //Init the location pluck on the users location
            initLocationComponent()
        }

        //Initialization of button actions
        binding.recenterLocation.setOnClickListener { recenterOnDeviceLocation() }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(com.mapbox.navigation.examples.R.menu.top_app_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            com.mapbox.navigation.examples.R.id.bike_status -> {
                // navigate to settings screen
                showSnackBar("Tienes una bici reservada por 10 minutos")
                true
            }
            com.mapbox.navigation.examples.R.id.route_status -> {
                bottomSheetBehaviorRoute.state = BottomSheetBehavior.STATE_EXPANDED
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSnackBar(text: String, snackbarDuration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(view!!, text, snackbarDuration)
            .setAction("OK") {}
            .show()
    }

    override fun onPrepareOptionsMenu(menu: Menu){
        super.onPrepareOptionsMenu(menu)
        val bikeStatusMenu = menu.findItem(com.mapbox.navigation.examples.R.id.bike_status)
        val routeStatusMenu = menu.findItem(com.mapbox.navigation.examples.R.id.route_status)
        bikeStatusMenu.isVisible = isReserved
        routeStatusMenu.isVisible = onRoute
    }

    private fun setOnRouteState(routeActive: Boolean){
        onRoute = routeActive
        requireActivity().invalidateOptionsMenu()
    }

    private fun recenterOnDeviceLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location ->
                val userLocationPoint = Point.fromLngLat(location.longitude, location.latitude)
                val cameraPosition = CameraOptions.Builder()
                    .zoom(15.0)
                    .center(userLocationPoint)
                    .build()
                // Move to the annotation in the map
                mapboxMap.easeTo(cameraPosition, mapAnimationOptions { duration(2000) })
            }
    }


    private fun setRouteWithBike(bike: Bike) {
        setOnRouteState(true)
        binding.bottomSheetContentStop.persistentBottomSheetStop.visibility = View.GONE
        binding.bottomSheetContentRoute.persistentBottomSheetRoute.visibility = View.VISIBLE
        binding.bottomSheetContentRoute.bikeId.text = bike.bike_id.toString()
        if (bike is ElectricBike){
            val batteryLevel = bike.battery.toString()
            binding.bottomSheetContentRoute.bikeBattery.text = "$batteryLevel %"
        }else{
            binding.bottomSheetContentRoute.bikeBattery.visibility = View.GONE
            binding.bottomSheetContentRoute.iconBattery.visibility = View.GONE
        }
        bottomSheetBehaviorRoute.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
            this.pulsingColor = R.color.primary_material_dark
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bottomSheetBehavior = null
        _bottomSheetBehaviorRoute = null
        _pointAnnotationManager = null
        _annotationApi = null
    }

    override fun onDestroy() {
        super.onDestroy()
        MapboxNavigationProvider.destroy()
        mapboxReplayer.finish()
    }

    private fun addAnnotationToMap(list: List<Stop>) {
        bitmapFromDrawableRes(
            this@HomeFragment.requireContext(),
            com.mapbox.navigation.examples.R.drawable.red_marker
        )?.let { it ->
            // Create an instance of the Annotation API and get the PointAnnotationManager.
            // Set options for the resulting symbol layer.
            val viewAnnotationManager = binding.mapView.viewAnnotationManager
            viewAnnotationManager.removeAllViewAnnotations()
            val listWithIcons = list.map { stop ->
                //Add the TextView over the Point with the total number of the bikes
                addViewAnnotation(stop, it)
                PointAnnotationOptions()
                    .withPoint(stop.location)
                    .withData(stop.toJson())
                    .withIconImage(it)
                    .withIconAnchor(IconAnchor.BOTTOM)
            }
            pointAnnotationManager.deleteAll()
            pointAnnotationManager.create(listWithIcons)

            pointAnnotationManager.addClickListener{ stopClicked ->
                val cameraPosition = CameraOptions.Builder()
                    .zoom(14.5)
                    .center(stopClicked.geometry)
                    .build()
                // Move to the annotation in the map
                mapboxMap.easeTo(cameraPosition, mapAnimationOptions { duration(2000) })

                //Open the bottom drawer
                openBottomDrawer()

                //Add the specific info of the stop to the bottom Drawer
                setStopInfo(stopClicked)

                true
            }
        }
    }

    private fun openBottomDrawer() {
        binding.bottomSheetContentStop.persistentBottomSheetStop.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        if (bottomSheetBehaviorRoute.state == BottomSheetBehavior.STATE_EXPANDED)
        {
            bottomSheetBehaviorRoute.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun addViewAnnotation(
        stop: Stop,
        it: Bitmap
    ) {
        val viewAnnotationManager = binding.mapView.viewAnnotationManager
        val viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = com.mapbox.navigation.examples.R.layout.annotation_view_number_stop,
            options = viewAnnotationOptions {
                geometry(stop.location)
                anchor(ViewAnnotationAnchor.BOTTOM)
                offsetY(it.height)
            }
        )
        AnnotationViewNumberStopBinding.bind(viewAnnotation).apply {
            numberBikes.text = stop.getTotalBikeCount().toString()
        }
    }

    private fun setStopInfo(stopClicked: PointAnnotation) {
        val data = JSONObject(stopClicked.getData().toString())
        binding.bottomSheetContentStop.stopTitle.text = data.get("address").toString()
        binding.bottomSheetContentStop.stopId.text = data.get("id").toString()
        binding.bottomSheetContentStop.countPedalBike.text = data.get("count_bike_pedal").toString()
        binding.bottomSheetContentStop.countElectricBike.text = data.get("count_bike_electric").toString()
        binding.bottomSheetContentStop.countBikeStop.text = data.get("count_bike_stop").toString()
        if (isReserved) {
            val button = binding.bottomSheetContentStop.reserveBikeButton
            button.isEnabled = false
            button.text = "Ya tienes una reserva activa"
        }
        //There is at least one space to lock the bike
        if (onRoute && data.get("count_bike_stop").toString().toInt()>0) {
            binding.bottomSheetContentStop.reserveBikeButton.visibility = View.GONE
            val button = binding.bottomSheetContentStop.scanQrOnStopButton
            button.visibility = View.VISIBLE
            button.isEnabled = true
        }
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    @OptIn(ExperimentalTime::class)
    fun experimentalConversion(seconds: Int?): String{
        val converted = seconds?.let { convert(it.toDouble(), DurationUnit.SECONDS, DurationUnit.MINUTES).minutes }
        return converted.toString()
    }

}