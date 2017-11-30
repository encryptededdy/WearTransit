package nz.zhang.aucklandtransportwear

import android.graphics.drawable.Icon
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText

class ComplicationProvider : ComplicationProviderService() {
    override fun onComplicationUpdate(complicationID: Int, dataType: Int, complicationManager: ComplicationManager?) {
        // We don't actually need to send live updates, we just need to set this up
        val complicationData = ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                .setShortText(ComplicationText.plainText("133"))
                .setIcon(Icon.createWithResource(applicationContext, R.drawable.ic_train_white_24dp))
                .build()

        complicationManager?.updateComplicationData(complicationID, complicationData)
    }
}