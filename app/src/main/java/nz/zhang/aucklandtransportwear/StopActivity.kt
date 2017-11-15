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
import nz.zhang.aucklandtransportwear.wakaapi.Trip
import nz.zhang.aucklandtransportwear.wakaapi.listener.StopInfoListener
import java.util.*
import kotlin.concurrent.fixedRateTimer
import android.support.wearable.activity.ConfirmationActivity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import com.google.android.wearable.intent.RemoteIntent





class StopActivity : WearableActivity() {

    lateinit var stop:Stop

    private lateinit var timer:Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)
        AndroidThreeTen.init(this)
        // Enables Always-on
        setAmbientEnabled()
        stop = intent.getParcelableExtra("stop")
        // Stop saving icon status
        if (DataStore.savedStops.contains(stop)) {
            starIcon.setImageDrawable(getDrawable(R.drawable.ic_star_white_24dp))
            starStopText.text = "Unsave this stop"
        } else {
            starIcon.setImageDrawable(getDrawable(R.drawable.ic_star_border_white_24dp))
            starStopText.text = "Save this stop"
        }
        serviceRecycler.layoutManager = LinearLayoutManager(this)
        serviceRecycler.isNestedScrollingEnabled = false
        populateMainFields()
        timer = fixedRateTimer("RefreshBoard", true,0, 20000) {
            populateRTBoard()
        }
    }

    override fun onEnterAmbient(ambientDetails: Bundle?) {
        mainLayout.setBackgroundColor(Color.BLACK)
        topHalf.setBackgroundColor(Color.BLACK)
        super.onEnterAmbient(ambientDetails)
    }

    override fun onExitAmbient() {
        mainLayout.setBackgroundColor(Color.parseColor("#1A237E"))
        topHalf.setBackgroundColor(Color.parseColor("#3F51B5"))
        super.onExitAmbient()
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
            override fun update(trips: List<Trip>?) {
                if (trips != null) {
                    System.out.println("Populating trips... (${trips.size}")
                    loadingServices.visibility = View.GONE
                    if (trips.isEmpty()) {
                        noServices.visibility = View.VISIBLE
                    } else {
                        val adapter = TripRecyclerAdapter(this@StopActivity, trips.sorted())
                        serviceRecycler.adapter = adapter
                        serviceRecycler.invalidate()
                    }
                }
            }

        })
    }

    fun saveStop(view: View) {
        if (DataStore.savedStops.contains(stop)) {
            // Delete the stop
            DataStore.savedStops.remove(stop)
            DataStore.writeData()
            starIcon.setImageDrawable(getDrawable(R.drawable.ic_star_border_white_24dp))
            starStopText.text = "Save this stop"
            val intent = Intent(this, ConfirmationActivity::class.java)
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION)
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Stop Unsaved")
            startActivity(intent)
        } else {
            // Save the stop
            DataStore.savedStops.add(stop)
            DataStore.writeData()
            starIcon.setImageDrawable(getDrawable(R.drawable.ic_star_white_24dp))
            starStopText.text = "Unsave this stop"
            val intent = Intent(this, ConfirmationActivity::class.java)
            intent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                    ConfirmationActivity.SUCCESS_ANIMATION)
            intent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Stop Saved")
            startActivity(intent)
        }
    }

    fun openOnPhone(view: View) {
        val intent = Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse("https://getwaka.com/s/${stop.stop_region}/${stop.stop_id}"))
        RemoteIntent.startRemoteActivity(this, intent, null)
        val animIntent = Intent(this, ConfirmationActivity::class.java)
        animIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE,
                ConfirmationActivity.OPEN_ON_PHONE_ANIMATION)
        animIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE, "Opened on Phone")
        startActivity(animIntent)
    }
}
