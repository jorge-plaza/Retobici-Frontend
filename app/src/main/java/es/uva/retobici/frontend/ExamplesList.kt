package es.uva.retobici.frontend

import android.content.Context
import androidx.core.content.ContextCompat
import com.mapbox.maps.R
import es.uva.retobici.frontend.camera.ShowCameraTransitionsActivity
import es.uva.retobici.frontend.fetchroute.FetchARouteActivity
import es.uva.retobici.frontend.waypoints.MultipleWaypointsActivity
import es.uva.retobici.frontend.turnbyturn.TurnByTurnExperienceActivity

fun Context.examplesList() = listOf(
    MapboxExample(
        ContextCompat.getDrawable(this, R.drawable.mapbox_logo_helmet),
        getString(com.mapbox.navigation.examples.R.string.title_turn_by_turn),
        getString(com.mapbox.navigation.examples.R.string.description_turn_by_turn),
        TurnByTurnExperienceActivity::class.java
    ),
    //hide
    MapboxExample(
        ContextCompat.getDrawable(this, R.drawable.mapbox_compass_icon),
        getString(com.mapbox.navigation.examples.R.string.title_fetch_route),
        getString(com.mapbox.navigation.examples.R.string.description_fetch_route),
        FetchARouteActivity::class.java
    ),
    //hide
    MapboxExample(
        ContextCompat.getDrawable(this, R.drawable.mapbox_compass_icon),
        getString(com.mapbox.navigation.examples.R.string.title_camera_transitions),
        getString(com.mapbox.navigation.examples.R.string.description_camera_transitions),
        ShowCameraTransitionsActivity::class.java
    ),
    //hide
    MapboxExample(
        ContextCompat.getDrawable(this, R.drawable.mapbox_compass_icon),
        getString(com.mapbox.navigation.examples.R.string.title_multiple_way_points),
        getString(com.mapbox.navigation.examples.R.string.description_multiple_way_points),
        MultipleWaypointsActivity::class.java
    ),
)
