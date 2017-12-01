package nz.zhang.aucklandtransportwear

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import kotlinx.android.synthetic.main.activity_stops_list.*
import nz.zhang.aucklandtransportwear.wakaapi.Stop

/**
 * Created by Edward Zhang on 30/11/2017.
 */

class StopsListPickerActivity : WearableActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_list)
        favTitle.text = "Select Stop"
        stopsListRecycler.layoutManager = LinearLayoutManager(this)
        stopsListRecycler.isNestedScrollingEnabled = false
        val filteredList = DataStore.savedStops.filter { it.stop_region == DataStore.selectedCity.shortCode }
        val adapter = StopRecyclerAdapter(this, filteredList, this)
        stopsListRecycler.adapter = adapter
    }

    fun selectedStop(stop: Stop) {
        DataStore.complicationStop = stop
        DataStore.writeData()
        setResult(RESULT_OK)
        finish()
    }
}