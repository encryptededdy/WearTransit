package nz.zhang.aucklandtransportwear

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.wear.widget.SwipeDismissFrameLayout
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient

import kotlinx.android.synthetic.main.activity_maps.*
import com.google.android.gms.location.LocationServices

const val DEFAULT_ZOOM = 15f

class MapsActivity : WearableActivity(), OnMapReadyCallback {

    /**
     * Map is initialized when it's fully loaded and ready to be used.
     * See [onMapReady]
     */
    private lateinit var gMap: GoogleMap
    private var locationAllowed = false
    lateinit private var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation:Location = Location("")

    init {
        lastKnownLocation.latitude = -36.844
        lastKnownLocation.longitude = 174.766
    }

    public override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        // Enables always on.
        setAmbientEnabled()

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

        // Inform user how to close app (Swipe-To-Close).
        val duration = Toast.LENGTH_LONG
        val toast = Toast.makeText(getApplicationContext(), R.string.intro_text, duration)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

        // Adds a marker in Sydney, Australia and moves the camera.
        val sydney = LatLng(-34.0, 151.0)
        gMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        gMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

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
                    // Don't need to check success as we already have a default location!
                    System.out.println("Gotlocation")
                    // Set the map's camera position to the current location of the device.
                    lastKnownLocation = task.result
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            LatLng(lastKnownLocation.latitude,
                                    lastKnownLocation.longitude), DEFAULT_ZOOM))
                }
            } else {
                // Move to default location
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        LatLng(lastKnownLocation.latitude,
                                lastKnownLocation.longitude), DEFAULT_ZOOM))
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message)
        }

    }


}
