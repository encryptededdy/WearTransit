package nz.zhang.aucklandtransportwear

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.View

class MainActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openMaps(view: View) {
        val mapsIntent = Intent(this, MapsActivity::class.java)
        startActivity(mapsIntent)
    }

    fun openFavourites(view: View) {
        val favesIntent = Intent(this, StopsListActivity::class.java)
        startActivity(favesIntent)
    }

    fun openCities(view: View) {
        val citiesIntent = Intent(this, CityPickerActivity::class.java)
        startActivity(citiesIntent)
    }
}
