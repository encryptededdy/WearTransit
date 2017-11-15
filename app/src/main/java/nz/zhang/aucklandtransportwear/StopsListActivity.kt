package nz.zhang.aucklandtransportwear

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.wearable.activity.WearableActivity
import kotlinx.android.synthetic.main.activity_stops_list.*
import nz.zhang.aucklandtransportwear.wakaapi.Stop

class StopsListActivity : WearableActivity() {

    lateinit var currentList:List<Stop>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stops_list)
        stopsListRecycler.layoutManager = LinearLayoutManager(this)
        stopsListRecycler.isNestedScrollingEnabled = false
        val filteredList = DataStore.savedStops.filter { it.stop_region == DataStore.selectedCity.shortCode }
        val adapter = StopRecyclerAdapter(this, filteredList)
        stopsListRecycler.adapter = adapter
        currentList = filteredList
    }

    override fun onResume() {
        val filteredList = DataStore.savedStops.filter { it.stop_region == DataStore.selectedCity.shortCode }
        if (filteredList != currentList) {
            val adapter = StopRecyclerAdapter(this, filteredList)
            stopsListRecycler.adapter = adapter
            stopsListRecycler.invalidate()
        }
        super.onResume()
    }
}
