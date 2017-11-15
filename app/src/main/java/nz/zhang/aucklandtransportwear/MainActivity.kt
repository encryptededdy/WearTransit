package nz.zhang.aucklandtransportwear

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View

class MainActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Enables Always-on
        setAmbientEnabled()
    }

    fun openMaps(view: View) {
        val mapsIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapsIntent)
    }

    fun openFavourites(view: View) {
        val favesIntent = Intent(this, StopsListActivity::class.java)
        startActivity(favesIntent)
    }
}
