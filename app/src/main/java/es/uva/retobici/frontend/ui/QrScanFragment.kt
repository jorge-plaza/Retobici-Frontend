package es.uva.retobici.frontend.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.zxing.Result
import com.mapbox.navigation.examples.databinding.FragmentScanQrBinding
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.MasterActivity
import es.uva.retobici.frontend.domain.model.ElectricBike
import es.uva.retobici.frontend.ui.home.HomeViewModel

private const val CAMERA_REQUEST_CODE = 101

@AndroidEntryPoint
class QrScanFragment : Fragment(){
    private lateinit var masterActivity: MasterActivity
    private lateinit var codeScanner: CodeScanner
    private var _binding: FragmentScanQrBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by activityViewModels()
    private var idScanned: Int? = null
    private var finalStopScanned: Boolean = false;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        idScanned = null
        masterActivity = activity as MasterActivity
        _binding = FragmentScanQrBinding.inflate(inflater, container, false)

        homeViewModel.loading.observe(this.viewLifecycleOwner){ masterActivity.loading(it) }

        homeViewModel.bikeAvailable.observe(this.viewLifecycleOwner) { bikeState ->
            when(bikeState){
                is QrBikeState.NoScanned -> {}
                is QrBikeState.QrScanned -> {
                    binding.routeButton.isEnabled = true
                    when(homeViewModel.route.value){
                        null -> {binding.routeButton.text = "Comenzar ruta"}
                        RouteState.NoRoute -> {binding.routeButton.text = "Comenzar ruta"}
                        is RouteState.ActiveRoute -> {binding.routeButton.text = "Finalizar ruta"}
                        is RouteState.FinishedRoute -> {binding.routeButton.text = "Comenzar ruta"}
                    }
                    binding.scannedBikeLayout.visibility = View.VISIBLE
                    binding.bikeScannedId.text = bikeState.bike.bike_id.toString()
                    if (bikeState.bike is ElectricBike) {
                        binding.bikeBatteryContent.text = "${bikeState.bike.battery}%"
                        binding.layoutBattery.visibility = View.VISIBLE
                    }
                    else binding.layoutBattery.visibility = View.INVISIBLE
                }
                is QrBikeState.QrScannedEnd -> {
                    binding.routeButton.isEnabled = true
                    binding.routeButton.text = "Finalizar ruta"
                    finalStopScanned = false
                }
            }
        }

        homeViewModel.route.observe(this.viewLifecycleOwner){ route ->
            when (route){
                is RouteState.NoRoute -> {
                    binding.routeButton.isEnabled = false
                    binding.routeButton.text = "Escanea el QR para Desbloquear"
                    //binding.scannedBikeLayout.visibility = View.VISIBLE
                }
                is RouteState.ActiveRoute -> {
                    binding.routeButton.isEnabled = false
                    binding.routeButton.text = "Escanea el QR para Finalizar"
                }
                is RouteState.FinishedRoute -> {
                    view?.findNavController()?.navigate(com.mapbox.navigation.examples.R.id.action_qr_scan_to_routeSummaryFragment)
                }
            }
        }

        //Unlock the bike
        binding.routeButton.setOnClickListener {
            when(homeViewModel.route.value){
                null -> {
                    homeViewModel.performRoute(null)
                    view?.findNavController()?.navigateUp()
                }
                is RouteState.NoRoute -> {
                    homeViewModel.performRoute(null)
                    view?.findNavController()?.navigateUp()
                }
                is RouteState.ActiveRoute -> {
                    if (homeViewModel.bikeAvailable.value is QrBikeState.QrScannedEnd){
                        val stopID = (homeViewModel.bikeAvailable.value as QrBikeState.QrScannedEnd).stop.id
                        homeViewModel.performRoute(stopID)
                    }
                }
                is RouteState.FinishedRoute -> {
                    view?.findNavController()?.navigate(com.mapbox.navigation.examples.R.id.action_qr_scan_to_routeSummaryFragment)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupPermissions()
        setUpScanner()
    }

    private fun setUpScanner() {
        val scannerView = binding.scannerView

        codeScanner = CodeScanner(this.requireContext(), scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            when(homeViewModel.route.value){
                null -> unlockBike(it)
                is RouteState.NoRoute -> unlockBike(it)
                is RouteState.ActiveRoute -> lockBike(it.text.toInt())
                is RouteState.FinishedRoute -> {}
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            Log.d("qr", "Camera initialization error: ${it.message}")
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

    }

    private fun unlockBike(it: Result) {
        if (idScanned == null || it.text.toInt() != idScanned) {
            idScanned = it.text.toInt()
            homeViewModel.getBike(it.text.toInt())
        }
    }

    private fun lockBike(stop: Int){
        if (!finalStopScanned){
            homeViewModel.setBike(stop)
            finalStopScanned = true
        }
    }

    override fun onDestroyView() {
        codeScanner.releaseResources()
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private fun setupPermissions(){
        val permission = ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED){
            makePermissionRequest()
        }
    }

        private fun makePermissionRequest() {
        ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.d("permission", "you need camera permission")
                }
                else{
                    //sucessful
                }
            }

        }
    }

}