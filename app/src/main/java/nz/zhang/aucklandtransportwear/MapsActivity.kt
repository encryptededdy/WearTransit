package nz.zhang.aucklandtransportwear

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback

import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.wear.widget.SwipeDismissFrameLayout
import android.support.wearable.activity.WearableActivity
import android.support.wearable.input.RotaryEncoder
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient

import kotlinx.android.synthetic.main.activity_maps.*
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import nz.zhang.aucklandtransportwear.wakaapi.Stop
import nz.zhang.aucklandtransportwear.wakaapi.StopType
import nz.zhang.aucklandtransportwear.wakaapi.WakaAPI
import nz.zhang.aucklandtransportwear.wakaapi.listener.StopSearchListener

const val DEFAULT_ZOOM = 16.5f
const val HIDE_ZOOM = 14f

class MapsActivity : WearableActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveListener {

    /**
     * Map is initialized when it's fully loaded and ready to be used.
     * See [onMapReady]
     */
    private lateinit var gMap: GoogleMap
    private var locationAllowed = false
    lateinit private var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation:Location = Location("")
    private var lastStopQueriedLocation:Location = lastKnownLocation

    private val addedStops:ArrayList<Stop> = ArrayList()
    private val addedMarkers:ArrayList<Marker> = ArrayList()

    private var lastZoom = DEFAULT_ZOOM

    init {
        lastKnownLocation.latitude = DataStore.selectedCity.defaultLat
        lastKnownLocation.longitude = DataStore.selectedCity.defaultLong
    }

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        setContentView(R.layout.activity_maps)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Enables the Swipe-To-Dismiss Gesture via the root layout (SwipeDismissFrameLayout).
        // Swipe-To-Dismiss is a standard pattern in Wear for closing an app and needs to be
        // manually enabled for any Google Maps Activity. For more information, review our docs:
        // https://developer.android.com/training/wearables/ui/exit.html
        swipe_dismiss_root_container.addCallback(object : SwipeDismissFrameLayout.Callback() {
            override fun onDismissed(layout: SwipeDismissFrameLayout?) {
                // Hides view before exit to avoid stutter.
                layout?.visibility = View.GONE
                finish()
            }
        })

        // Adjusts margins to account for the system window insets when they become available.
        swipe_dismiss_root_container.setOnApplyWindowInsetsListener { _, insetsArg ->
            val insets = swipe_dismiss_root_container.onApplyWindowInsets(insetsArg)

            val params = map_container.layoutParams as FrameLayout.LayoutParams

            // Add Wearable insets to FrameLayout container holding map as margins
            params.setMargins(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom)
            map_container.layoutParams = params

            insets
        }

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        val mapFragment = map as MapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Map is ready to be used.
        gMap = googleMap

        map_container.setOnGenericMotionListener(View.OnGenericMotionListener { v, ev ->
            if (ev?.action == MotionEvent.ACTION_SCROLL && RotaryEncoder.isFromRotaryEncoder(ev)) {
                // Don't forget the negation here
                val delta = -RotaryEncoder.getRotaryAxisValue(ev) * RotaryEncoder.getScaledScrollFactor(this@MapsActivity)
                gMap.animateCamera(CameraUpdateFactory.zoomBy(delta))
                return@OnGenericMotionListener true
            }

            false;
        })

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.maps_style))

            if (!success) {
                Log.e("MapsStyle", "Style parsing failed.");
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapsStyle", "Can't find style. Error: ", e);
        }

        gMap.setOnMarkerClickListener(this)
        gMap.setOnCameraIdleListener(this)
        gMap.setOnCameraMoveListener(this)

        // Inform user how to close app (Swipe-To-Close).
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(getApplicationContext(), R.string.intro_text, duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationAllowed = true
        } else {
            //TODO: Ask for location perms from user
            ActivityCompat.requestPermissions(this, Array(1){android.Manifest.permission.ACCESS_FINE_LOCATION},0)
        }
        updateLocationUI()
        getDeviceLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        // If request is cancelled, the result arrays are empty.
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            locationAllowed = true
        }
        updateLocationUI()
        getDeviceLocation()
    }

    private fun updateLocationUI() {
        if (locationAllowed) {
            try {
                gMap.isMyLocationEnabled = true
            } catch (e:SecurityException) {
                e.printStackTrace()
            }
        }
    }

    private fun getDeviceLocation() {
        /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (locationAllowed) {
                System.out.println("Location allowed, now asking for location")
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.result != null) {
                        System.out.println("Gotlocation")
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation.latitude,
                                        lastKnownLocation.longitude), DEFAULT_ZOOM))
                    } else {
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                LatLng(lastKnownLocation.latitude,
                                        lastKnownLocation.longitude), DEFAULT_ZOOM))
                    }
                }
            } else {
                // Move to default location
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastKnownLocation.latitude,
                                lastKnownLocation.longitude), DEFAULT_ZOOM))
            }
            populateStops(lastKnownLocation)
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message ?: e.toString())
        }

    }

    private fun populateStops(location: Location) {
        lastStopQueriedLocation = location
        WakaAPI().searchStopsGeo(location.latitude, location.longitude, 1000, object : StopSearchListener {
            override fun update(stops: List<Stop>?) {
                if (stops != null) {
                    stops.forEach { stop:Stop ->
                        if (!addedStops.contains(stop)) {
                            val marker = gMap.addMarker(MarkerOptions()
                                    .position(LatLng(stop.stop_lat, stop.stop_lon)))
                            when (stop.stopType()) {
                                StopType.TRAIN -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.train_pin))
                                StopType.BUS -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.bus_pin))
                                StopType.FERRY -> marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ferry_pin))
                            }
                            marker.tag = stop
                            addedStops.add(stop)
                            addedMarkers.add(marker)
                            if (gMap.cameraPosition.zoom < HIDE_ZOOM) {
                                marker.isVisible = false
                            }
                        }
                    }
                } else {
                    Log.e("AT API", "Failed to get stops")
                }
            }

        })
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        if (p0 != null) {
            val stop = p0.tag as Stop
            val stopIntent = Intent(this, StopActivity::class.java)
            stopIntent.putExtra("stop", stop)
            startActivity(stopIntent)
        }
        return true
    }

    override fun onCameraIdle() {
        val currentPosition = Location("")
        currentPosition.latitude = gMap.cameraPosition.target.latitude
        currentPosition.longitude = gMap.cameraPosition.target.longitude
        if (lastStopQueriedLocation.distanceTo(currentPosition) > 500) {
            Log.i("BusMap", "Moved far enough, we should re-query for stops now")
            populateStops(currentPosition)
        }
    }

    override fun onCameraMove() {
        val zoom = gMap.cameraPosition.zoom
        if (lastZoom > HIDE_ZOOM && zoom < HIDE_ZOOM) {
            addedMarkers.forEach { it.isVisible = false }
        } else if (lastZoom < HIDE_ZOOM && zoom > HIDE_ZOOM) {
            addedMarkers.forEach { it.isVisible = true }
        }
        lastZoom = zoom
    }

}
