package es.uva.retobici.frontend

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.get
import androidx.lifecycle.asLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.navigation.examples.databinding.ActivityMasterBinding
import com.mapbox.navigation.examples.R
import dagger.hilt.android.AndroidEntryPoint
import es.uva.retobici.frontend.data.repositories.UserPreferences
import es.uva.retobici.frontend.ui.viewmodels.LoginViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MasterActivity: AppCompatActivity(), PermissionsListener {

    @Inject lateinit var userPreferences: UserPreferences
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMasterBinding
    private val permissionsManager = PermissionsManager(this)
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (!isMapboxTokenProvided()) {
            showNoTokenErrorDialog()
            return
        }

        binding = ActivityMasterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!PermissionsManager.areLocationPermissionsGranted(this)) {
            permissionsManager.requestLocationPermissions(this)
        }

        setSupportActionBar(binding.appBarMaster.topAppBar)

        binding.appBarMaster.progressIndicator.visibility = View.GONE

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_master) as NavHostFragment
        val navController: NavController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.loginFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        loginViewModel.logoutResult.observe(this){ logout ->
            if (logout){
                Snackbar.make(binding.root,
                    "Se ha cerrado sesión correctamente",
                    Snackbar.LENGTH_LONG)
                    .setAction("OK") {}
                    .show()
            }
        }

        userPreferences.email.asLiveData().observe(this){ email ->
            val linearLayout = binding.navView.getHeaderView(0) as LinearLayout
            val profileImage = linearLayout[0]
            val userEmail = linearLayout[1] as TextView
            val logout = binding.navView[1] as TextView
            val login = binding.navView.menu.findItem(R.id.loginFragment)
            if (email != null && email != "invalid") {
                profileImage.visibility = View.VISIBLE
                logout.visibility = View.VISIBLE
                login.isVisible = false
                userEmail.text = email
            }else{
                profileImage.visibility = View.GONE
                logout.visibility = View.GONE
                login.isVisible = true
                userEmail.text = getString(R.string.User_not_loggedin)
            }
        }

        userPreferences.points.asLiveData().observe(this){ points ->
            val linearLayout = binding.navView.getHeaderView(0) as LinearLayout
            val profileImage = linearLayout[0]
            val userPoints = linearLayout[2] as TextView
            val logout = binding.navView[1] as TextView
            val login = binding.navView.menu.findItem(R.id.loginFragment)
            if (points != -1 && points != null) {
                profileImage.visibility = View.VISIBLE
                logout.visibility = View.VISIBLE
                login.isVisible = false
                userPoints.text = "Puntos: $points"
            }else{
                profileImage.visibility = View.GONE
                logout.visibility = View.GONE
                login.isVisible = true
                userPoints.text = getString(R.string.user_not_logged_desc)
            }
        }

        val logoutButton = binding.navView[1] as TextView
        logoutButton.setOnClickListener {
            loginViewModel.logOut()
            drawerLayout.close()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_master)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
        Toast.makeText(
            this,
            "This app needs location and storage permissions in order to show its functionality.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            requestStoragePermission()
        } else {
            Toast.makeText(
                this,
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
            ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionsNeeded.add(permission)
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                10
            )
        }
    }

    private fun isMapboxTokenProvided() =
        getString(R.string.mapbox_access_token) != MAPBOX_ACCESS_TOKEN_PLACEHOLDER

    private fun showNoTokenErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.noTokenDialogTitle))
            .setMessage(getString(R.string.noTokenDialogBody))
            .setCancelable(false)
            .setPositiveButton("Ok") { _, _ ->
                finish()
            }
            .show()
    }

    fun loading(visible: Boolean){
        if (visible){
            binding.appBarMaster.progressIndicator.visibility = View.VISIBLE
        }else{
            binding.appBarMaster.progressIndicator.visibility = View.INVISIBLE
        }
    }
}
private const val MAPBOX_ACCESS_TOKEN_PLACEHOLDER = "YOUR_MAPBOX_ACCESS_TOKEN_GOES_HERE"