package es.uva.retobici.frontend.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.Chronometer
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.bindgen.Expected
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
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.navigation.R
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.replay.MapboxReplayer
import com.mapbox.navigation.core.replay.ReplayLocationEngine
import com.mapbox.navigation.core.replay.route.ReplayProgressObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver
import com.mapbox.navigation.examples.databinding.AnnotationViewNumberStopBinding
import com.mapbox.navigation.examples.databinding.FragmentHomeBinding
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer
import com.mapbox.navigation.ui.maneuver.api.MapboxManeuverApi
import com.mapbox.navigation.ui.maneuver.view.MapboxManeuverView
import com.mapbox.navigation.ui.maps.camera.NavigationCamera
import com.mapbox.navigation.ui.maps.camera.data.MapboxNavigationViewportDataSource
import com.mapbox.navigation.ui.maps.camera.transition.NavigationCameraTransitionOptions
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import com.mapbox.navigation.ui.tripprogress.api.MapboxTripProgressApi
import com.mapbox.navigation.ui.tripprogress.model.*
import com.mapbox.navigation.ui.tripprogress.view.MapboxTripProgressView
import com.mapbox.navigation.ui.voice.api.MapboxSpeechApi
import com.mapbox.navigation.ui.voice.api.MapboxVoiceInstructionsPlayer
import com.mapbox.navigation.ui.voice.model.SpeechAnnouncement
import com.mapbox.navigation.ui.voice.model.SpeechError
import com.mapbox.navigation.ui.voice.model.SpeechValue
import com.mapbox.navigation.ui.voice.model.SpeechVolume
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.MasterActivity
import es.uva.retobici.frontend.domain.model.Bike
import es.uva.retobici.frontend.domain.model.ElectricBike
import es.uva.retobici.frontend.domain.model.Stop
//import es.uva.retobici.frontend.turnbyturn.MAPBOX_ACCESS_TOKEN_PLACEHOLDER
import org.json.JSONObject
import java.util.*
import kotlin.time.Duration.Companion.convert
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@AndroidEntryPoint
class HomeFragment : Fragment(), PermissionsListener {

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



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var isReserved = false
    private var onRoute = false

    //private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    //private lateinit var bottomSheetBehaviorRoute: BottomSheetBehavior<LinearLayout>

    private val homeViewModel : HomeViewModel by activityViewModels()

    // declare a global variable of FusedLocationProviderClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    /**
     * Debug tool used to play, pause and seek route progress events that can be used to produce mocked location updates along the route.
     */
    private val mapboxReplayer = MapboxReplayer()

    /**
     * Debug tool that mocks location updates with an input from the [mapboxReplayer].
     */
    private val replayLocationEngine = ReplayLocationEngine(mapboxReplayer)

    /**
     * Debug observer that makes sure the replayer has always an up-to-date information to generate mock updates.
     */
    private val replayProgressObserver = ReplayProgressObserver(mapboxReplayer)

    /**
     * Mapbox Maps entry point obtained from the [MapView].
     * You need to get a new reference to this object whenever the [MapView] is recreated.
     */
    private lateinit var mapboxMap: MapboxMap



    private val permissionsManager = PermissionsManager(this)

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(
            this.requireContext(),
            "This app needs location and storage permissions in order to show its functionality.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            requestStoragePermission()
        } else {
            Toast.makeText(
                this.requireContext(),
                "You didn't grant the permissions required to use the app",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun requestStoragePermission() {
        val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
        val permissionsNeeded: MutableList<String> = ArrayList()
        if (
            ContextCompat.checkSelfPermission(this.requireContext(), permission) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(permission)
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                permissionsNeeded.toTypedArray(),
                10
            )
        }
    }

    //private fun isMapboxTokenProvided() = getString(com.mapbox.navigation.examples.R.string.mapbox_access_token) != MAPBOX_ACCESS_TOKEN_PLACEHOLDER

    /**
    private fun showNoTokenErrorDialog() {
        AlertDialog.Builder(this.requireContext())
            .setTitle(getString(com.mapbox.navigation.examples.R.string.noTokenDialogTitle))
            .setMessage(getString(com.mapbox.navigation.examples.R.string.noTokenDialogBody))
            .setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->
                finish()
            }
            .show()
    }*/

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
        masterActivity.loading(true)

        homeViewModel.stops.observe(this.viewLifecycleOwner) { stops ->
            //TODO check the await or something that checks if the maps have been created
            addAnnotationToMap(stops)
            masterActivity.loading(false)
        }

        homeViewModel.unlockedBike.observe(this.viewLifecycleOwner) { bike ->

        }

        homeViewModel.reserved.observe(this.viewLifecycleOwner){ reservation ->
            masterActivity.loading(false)
            setReservationState(reservation)
        }

        homeViewModel.route.observe(this.viewLifecycleOwner){ route ->
            if (route == null){
                Log.d("route", "la ruta es null")
                //Initial state
                binding.bottomSheetContentRoute.persistentBottomSheetRoute.visibility = View.GONE
                binding.recenterLocation.layoutParams
                //Hide top icon
                setOnRouteState(false)
            }else if (route.final_stop != null && route.points != null) {
                //When the route is started
                binding.bottomSheetContentRoute.persistentBottomSheetRoute.visibility = View.GONE
                view?.findNavController()?.navigate(com.mapbox.navigation.examples.R.id.action_nav_home_to_routeSummaryFragment)
            }
            else if (route.final_stop == null && route.points == null){
                masterActivity.loading(true)
                setRouteWithBike(homeViewModel.unlockedBike.value!!)
            }
        }



        homeViewModel.seconds.observe(this.viewLifecycleOwner){ seconds ->
            binding.bottomSheetContentRoute.routeDurationTimer.text = experimentalConversion(seconds)
        }

        //bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContentStop.persistentBottomSheetStop)
        //bottomSheetBehaviorRoute = BottomSheetBehavior.from(binding.bottomSheetContentRoute.persistentBottomSheetRoute)
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
                        masterActivity.loading(true)
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
            val button = binding.bottomSheetContentStop.reserveBikeButton
            button.isEnabled = true
            button.text = "Reservar Bici Eléctrica"
            button.setOnClickListener { homeViewModel.reserveElectricBike() }
        }

        binding.bottomSheetContentStop.scanQrOnStopButton.setOnClickListener {
            //TODO go to qr scan view to end route
            view?.findNavController()?.navigate(com.mapbox.navigation.examples.R.id.action_nav_home_to_qr_scan)
        }

        /*
        binding.bottomSheetContentRoute.stopRouteButton.setOnClickListener {
            binding.bottomSheetContentRoute.routeDuration.text
            val elapsedMillis:Long = SystemClock.elapsedRealtime() - binding.bottomSheetContentRoute.routeDuration.base;
            val seconds: Int = (elapsedMillis/1000).toInt()
            homeViewModel.finishRoute(2,seconds)
        }

         */

        //Init annotation api for only use one instance
        _annotationApi = binding.mapView.annotations
        _pointAnnotationManager = annotationApi?.createPointAnnotationManager()


        mapboxMap = binding.mapView.getMapboxMap()

        // load map style
        mapboxMap.loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            //Init the location pluck on the users location
            initLocationComponent()
            //Init the gesture Listener for the map used when you move the map rotate or zoom
            //setupGesturesListener()
            // add long click listener that search for a route to the clicked destination
            //TODO paired with the other of async, previously the annotations were added here
            //addAnnotationToMap(list)
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
                true
            }
            com.mapbox.navigation.examples.R.id.search -> {
                // navigate to settings screen
                true
            }
            com.mapbox.navigation.examples.R.id.more -> {
                // save profile changes
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

    private fun setReservationState(reservation: Boolean) {
        if (reservation){
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showSnackBar("Tienes una bici reservada por 10 minutos")
        }
        isReserved = reservation
        requireActivity().invalidateOptionsMenu()
    }

    private fun setOnRouteState(routeActive: Boolean){
        onRoute = routeActive
        requireActivity().invalidateOptionsMenu()
    }

    private fun recenterOnDeviceLocation() {
        //TODO check permission
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
        binding.bottomSheetContentRoute.bikeId.text = bike.id.toString()
        if (bike is ElectricBike){
            val batteryLevel = bike.battery.toString()
            binding.bottomSheetContentRoute.bikeBattery.text = "$batteryLevel %"
        }else{
            binding.bottomSheetContentRoute.bikeBattery.visibility = View.GONE
            binding.bottomSheetContentRoute.iconBattery.visibility = View.GONE
        }
        bottomSheetBehaviorRoute.state = BottomSheetBehavior.STATE_EXPANDED
        //binding.bottomSheetContentRoute.routeDuration.start()
    }

    private fun setupGesturesListener() {
        binding.mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = binding.mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
            this.pulsingColor = R.color.primary_material_dark
        }
        /** This two listeners are used to track the user location */
        //locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        //locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    /**
     * Listener for map movement
     */
    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            //onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    /**
     * Remove the listeners because the map have been moved, so no tracking
     */
    private fun onCameraTrackingDismissed() {
        Toast.makeText(this.requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        binding.mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        //binding.mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        binding.mapView.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapboxMap.setCamera(CameraOptions.Builder().center(it).build())
        //binding.mapView.gestures.focalPoint = binding.mapView.getMapboxMap().pixelForCoordinate(it)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _bottomSheetBehavior = null
        _bottomSheetBehaviorRoute = null
        _pointAnnotationManager = null
        _annotationApi = null
    }

    override fun onStart() {
        super.onStart()
        // register event listeners
    }

    override fun onStop() {
        super.onStop()

        /** Commented because now these listeners are not attached on the start @see initLocationComponent*/
        binding.mapView.location.removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        binding.mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        binding.mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        MapboxNavigationProvider.destroy()
        mapboxReplayer.finish()
        //maneuverApi.cancel()
        //routeLineApi.cancel()
        //routeLineView.cancel()
        //speechApi.cancel()
        //voiceInstructionsPlayer.shutdown()
    }

    override fun onResume() {
        super.onResume()
    }

    /*
    private fun findRoute(destination: Point) {
        val originLocation = navigationLocationProvider.lastLocation
        val originPoint = originLocation?.let {
            Point.fromLngLat(it.longitude, it.latitude)
        } ?: return

        // execute a route request
        // it's recommended to use the
        // applyDefaultNavigationOptions and applyLanguageAndVoiceUnitOptions
        // that make sure the route request is optimized
        // to allow for support of all of the Navigation SDK features
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .applyDefaultNavigationOptions()
                .applyLanguageAndVoiceUnitOptions(this.requireContext())
                .coordinatesList(listOf(originPoint, destination))
                // provide the bearing for the origin of the request to ensure
                // that the returned route faces in the direction of the current user movement
                .bearingsList(
                    listOf(
                        Bearing.builder()
                            .angle(originLocation.bearing.toDouble())
                            .degrees(45.0)
                            .build(),
                        null
                    )
                )
                .layersList(listOf(mapboxNavigation.getZLevel(), null))
                .build(),
            object : RouterCallback {
                override fun onRoutesReady(
                    routes: List<DirectionsRoute>,
                    routerOrigin: RouterOrigin
                ) {
                    setRouteAndStartNavigation(routes)
                }

                override fun onFailure(
                    reasons: List<RouterFailure>,
                    routeOptions: RouteOptions
                ) {
                    // no impl
                }

                override fun onCanceled(routeOptions: RouteOptions, routerOrigin: RouterOrigin) {
                    // no impl
                }
            }
        )
    }
    */

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
            pointAnnotationManager?.deleteAll()
            pointAnnotationManager?.create(listWithIcons)

            pointAnnotationManager?.addClickListener{ stopClicked ->
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
            } ?: Log.d("pointAnnotationManager", "pointannotationmanager puede ser null algo falla")
        }
    }

    private fun openBottomDrawer() {
        binding.bottomSheetContentStop.persistentBottomSheetStop.visibility = View.VISIBLE
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
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
        //TODO calculate the distance from the user to the stop
        //binding.persistentBottomSheetStops.stopDistance = data.get("title").toString()
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

    private fun View.toggleViewty() {
        visibility = if (visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    @OptIn(ExperimentalTime::class)
    fun experimentalConversion(seconds: Int?): String{
        val converted = seconds?.let { convert(it.toDouble(), DurationUnit.SECONDS, DurationUnit.MINUTES).minutes }
        return converted.toString()
    }

}