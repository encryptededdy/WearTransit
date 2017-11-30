package nz.zhang.aucklandtransportwear

import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText

class ComplicationProvider : ComplicationProviderService() {
    override fun onComplicationUpdate(complicationID: Int, dataType: Int, complicationManager: ComplicationManager?) {
        // Build the intent we will use to open the stop
        val stopIntent = Intent(applicationContext, StopActivity::class.java)
        stopIntent.putExtra("stop", DataStore.savedStops[0])
        val pendingIntent = PendingIntent.getActivity(this, 0, stopIntent, 0)
        // We don't actually need to send live updates, we just need to set this up
        val complicationData = ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                .setShortText(ComplicationText.plainText("133"))
                .setIcon(Icon.createWithResource(applicationContext, R.drawable.ic_train_white_24dp))
                .setTapAction(pendingIntent)
                .build()

        complicationManager?.updateComplicationData(complicationID, complicationData)
    }
}