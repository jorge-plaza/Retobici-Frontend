package es.uva.retobici.utilities

import android.util.Log
import com.mapbox.android.core.location.LocationEngineCallback
import com.mapbox.android.core.location.LocationEngineResult
import es.uva.retobici.frontend.MasterActivity
import java.lang.ref.WeakReference

public class LocationListeningCallback internal constructor(activity: MasterActivity) :
    LocationEngineCallback<LocationEngineResult> {

    private val activityWeakReference: WeakReference<MasterActivity> = WeakReference(activity)

    override fun onSuccess(result: LocationEngineResult) {
        // The LocationEngineCallback interface's method which fires when the device's location has changed.
        result.lastLocation
        Log.d("prueba", result.lastLocation.toString())
    }

    /**
     * The LocationEngineCallback interface's method which fires when the device's location can not be captured
     *
     * @param exception the exception message
     */
    override fun onFailure(exception: Exception) {

        // The LocationEngineCallback interface's method which fires when the device's location can not be captured



    }
}