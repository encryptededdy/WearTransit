package nz.zhang.aucklandtransportwear

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import kotlinx.android.synthetic.main.activity_stop.*
import nz.zhang.aucklandtransportwear.atapi.Stop
import nz.zhang.aucklandtransportwear.atapi.StopType

class StopActivity : WearableActivity() {

    lateinit var stop:Stop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)

        // Enables Always-on
        setAmbientEnabled()
        stop = intent.getParcelableExtra("stop")
        populateMainFields()
    }

    private fun populateMainFields() {
        stopName.text = stop.stop_name
        when (stop.stopType()) {
            StopType.TRAIN -> typeIcon.setImageDrawable(getDrawable(R.drawable.ic_train_white_24dp))
            StopType.BUS -> typeIcon.setImageDrawable(getDrawable(R.drawable.ic_directions_bus_white_24dp))
            StopType.FERRY -> typeIcon.setImageDrawable(getDrawable(R.drawable.ic_directions_boat_white_24dp))
        }
    }
}
