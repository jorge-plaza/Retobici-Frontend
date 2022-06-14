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
import com.mapbox.navigation.examples.databinding.FragmentScanQrBinding
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.ui.CAMERA_REQUEST_CODE
import es.uva.retobici.frontend.ui.home.HomeViewModel

private const val CAMERA_REQUEST_CODE = 101

@AndroidEntryPoint
class QrScanFragment : Fragment(){

    private lateinit var codeScanner: CodeScanner
    private var _binding: FragmentScanQrBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val homeViewModel : HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanQrBinding.inflate(inflater, container, false)
        homeViewModel.unlockedBike.observe(this.viewLifecycleOwner) {
            binding.startTripProgressBar.visibility = View.INVISIBLE
            binding.startRouteButton.visibility = View.VISIBLE
        }

        homeViewModel.route.observe(this.viewLifecycleOwner){
            Log.d("hola", "llega aqui")
            view?.findNavController()?.navigateUp()
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
            activity?.runOnUiThread {
                binding.bikeScannedText.text = it.text.toString()
                binding.startTripProgressBar.visibility = View.VISIBLE
            }
            homeViewModel.unlockBike(it.text.toInt())
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            Log.d("qr", "Camera initialization error: ${it.message}")
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        //Unlock the bike
        binding.startRouteButton.setOnClickListener {
            binding.startTripProgressBar.visibility = View.VISIBLE
            binding.startRouteButton.visibility = View.INVISIBLE
            Log.d("hola","click en el boton")
            homeViewModel.startRoute()
        }
    }

    override fun onDestroyView() {
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