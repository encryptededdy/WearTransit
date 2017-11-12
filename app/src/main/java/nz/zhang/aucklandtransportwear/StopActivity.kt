package nz.zhang.aucklandtransportwear

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import android.view.View
import com.jakewharton.threetenabp.AndroidThreeTen
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
        AndroidThreeTen.init(this)
        // Enables Always-on
        setAmbientEnabled()
        stop = intent.getParcelableExtra("stop")
        serviceRecycler.layoutManager = LinearLayoutManager(this)
        serviceRecycler.isNestedScrollingEnabled = false
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
        ATAPI().getRTBoard(stop, 24, object:RTBoardListener {
            override fun update(services: List<ServiceRT>?) {
                if (services != null) {
                    System.out.println("Populating services...")
                    loadingServices.visibility = View.GONE
                    val adapter = ServiceRTAdapter(this@StopActivity, services.sorted())
                    serviceRecycler.adapter = adapter
                    serviceRecycler.invalidate()
                }
            }

        })
    }
}
