package nz.zhang.aucklandtransportwear

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText
import nz.zhang.aucklandtransportwear.wakaapi.StopType

class ComplicationProvider : ComplicationProviderService() {
    override fun onComplicationUpdate(complicationID: Int, dataType: Int, complicationManager: ComplicationManager?) {
        // Get the stop
        val stop = DataStore.complicationStop
        if (stop == null) { // do nothing if no stop is selected
            complicationManager?.noUpdateRequired(complicationID)
            return
        }
        // Build the intent we will use to open the stop
        val stopIntent = Intent(applicationContext, StopActivity::class.java)
        stopIntent.putExtra("stop", stop)
        val pendingIntent = PendingIntent.getActivity(this, 0, stopIntent, 0)

        val icon: Icon
        // Get the icon we need
        icon = when (stop.stopType()) {
            StopType.TRAIN -> Icon.createWithResource(applicationContext, R.drawable.ic_train_white_24dp)
            StopType.BUS -> Icon.createWithResource(applicationContext, R.drawable.ic_directions_bus_white_24dp)
            else -> Icon.createWithResource(applicationContext, R.drawable.ic_directions_boat_white_24dp)
        }

        // We don't actually need to send live updates, we just need to set this up
        val complicationData = ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                .setShortText(ComplicationText.plainText(stop.stop_id))
                .setIcon(icon)
                .setTapAction(pendingIntent)
                .build()

        complicationManager?.updateComplicationData(complicationID, complicationData)
    }
}