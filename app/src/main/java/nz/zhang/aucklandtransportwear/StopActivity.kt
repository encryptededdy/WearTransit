package nz.zhang.aucklandtransportwear

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import android.view.View
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.android.synthetic.main.activity_stop.*
import nz.zhang.aucklandtransportwear.wakaapi.Stop
import nz.zhang.aucklandtransportwear.wakaapi.StopType
import nz.zhang.aucklandtransportwear.wakaapi.WakaAPI
import nz.zhang.aucklandtransportwear.wakaapi.WakaService
import nz.zhang.aucklandtransportwear.wakaapi.listener.StopInfoListener
import java.util.*
import kotlin.concurrent.fixedRateTimer

class StopActivity : WearableActivity() {

    lateinit var stop:Stop

    lateinit var timer:Timer

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
        timer = fixedRateTimer("RefreshBoard", true,0, 20000) {
            populateRTBoard()
        }
    }

    override fun onStop() {
        timer.cancel()
        super.onStop()
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
        WakaAPI().getStopInfo(stop, object:StopInfoListener {
            override fun update(services: List<WakaService>?) {
                if (services != null) {
                    System.out.println("Populating services... (${services.size}")
                    loadingServices.visibility = View.GONE
                    if (services.isEmpty()) {
                        noServices.visibility = View.VISIBLE
                    } else {
                        val adapter = ServiceRTAdapter(this@StopActivity, services.sorted())
                        serviceRecycler.adapter = adapter
                        serviceRecycler.invalidate()
                    }
                }
            }

        })
    }
}
