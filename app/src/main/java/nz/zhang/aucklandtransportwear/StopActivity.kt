package nz.zhang.aucklandtransportwear

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import kotlinx.android.synthetic.main.activity_stop.*
import nz.zhang.aucklandtransportwear.atapi.ATAPI
import nz.zhang.aucklandtransportwear.atapi.ServiceRT
import nz.zhang.aucklandtransportwear.atapi.Stop
import nz.zhang.aucklandtransportwear.atapi.StopType
import nz.zhang.aucklandtransportwear.atapi.listener.RTBoardListener

class StopActivity : WearableActivity() {

    lateinit var stop:Stop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)

        // Enables Always-on
        setAmbientEnabled()
        stop = intent.getParcelableExtra("stop")
        populateMainFields()
        populateRTBoard()
    }

    private fun populateMainFields() {
        stopName.text = stop.stop_name
        when (stop.stopType()) {
            StopType.TRAIN -> typeIcon.setImageDrawable(getDrawable(R.drawable.ic_train_white_24dp))
            StopType.BUS -> typeIcon.setImageDrawable(getDrawable(R.drawable.ic_directions_bus_white_24dp))
            StopType.FERRY -> typeIcon.setImageDrawable(getDrawable(R.drawable.ic_directions_boat_white_24dp))
        }
    }

    private fun populateRTBoard() {
        ATAPI().getRTBoard(stop, 12, object:RTBoardListener {
            override fun update(services: List<ServiceRT>?) {
                if (services != null) {
                    services.forEach { service ->
                        System.out.println("Service ${service.destinationDisplay} arriving at ${service.expectedArrivalTime}, scheduled: ${service.scheduledArrivalTime}")
                    }
                }
            }

        })
    }
}
