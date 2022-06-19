package es.uva.retobici.frontend.ui

import android.content.pm.PackageManager
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import es.uva.retobici.frontend.core.Timer
import es.uva.retobici.frontend.domain.model.ElectricBike
import es.uva.retobici.frontend.ui.CAMERA_REQUEST_CODE
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        masterActivity = activity as MasterActivity
        _binding = FragmentScanQrBinding.inflate(inflater, container, false)
        homeViewModel.unlockedBike.observe(this.viewLifecycleOwner) { bike ->
            masterActivity.loading(false)
            if (bike != null){
                binding.startRouteButton.isEnabled = true
                binding.scannedBikeLayout.visibility = View.VISIBLE
                binding.bikeScannedId.text = bike.id.toString()
                if (bike is ElectricBike) {
                    binding.bikeBatteryContent.text = "${bike.battery}%"
                    binding.layoutBattery.visibility = View.VISIBLE
                }
                else binding.layoutBattery.visibility = View.INVISIBLE

                binding.startRouteButton.isEnabled = true
                binding.startRouteButton.text = "Comenzar ruta"
            }

        }

        homeViewModel.route.observe(this.viewLifecycleOwner){ route ->
            //No route started set Initial state for the UI
            if (route == null){
                binding.startRouteButton.isEnabled = false
                binding.startRouteButton.text = "Escanea el QR para Desbloquear"
                binding.scannedBikeLayout.visibility = View.VISIBLE
            }
            if (route!=null){
                binding.startRouteButton.visibility = View.GONE
            }
            if (route?.final_stop != null && route.points != null){
                masterActivity.loading(false)
                view?.findNavController()?.navigate(com.mapbox.navigation.examples.R.id.action_qr_scan_to_routeSummaryFragment)
            }
        }

        //Unlock the bike
        binding.startRouteButton.setOnClickListener {
            masterActivity.loading(true)
            binding.startRouteButton.isEnabled = false
            it.visibility = View.INVISIBLE
            homeViewModel.startRoute()
            view?.findNavController()?.navigateUp()
        }

        binding.endRouteButton.setOnClickListener {
            it.visibility = View.GONE
            it.isEnabled = false
            binding.startRouteButton.visibility = View.GONE
            masterActivity.loading(true)
            homeViewModel.finishRoute(2)
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
            if (homeViewModel.route.value!=null && homeViewModel.seconds.value != null && homeViewModel.seconds.value!! >0){
                //A Route have been initialized and the timer is running
                activity?.runOnUiThread {
                    binding.endRouteButton.visibility = View.VISIBLE
                    binding.endRouteButton.isEnabled = true
                }
            }
            else{
                unlockBike(it)
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

            activity?.runOnUiThread {
                masterActivity.loading(true)
            }
            homeViewModel.unlockBike(it.text.toInt())
        }
    }

    override fun onDestroyView() {
        codeScanner.releaseResources()
        super.onDestroyView()
        _binding = null
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
        var permission = ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.CAMERA)
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