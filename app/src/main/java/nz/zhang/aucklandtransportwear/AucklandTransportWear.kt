package nz.zhang.aucklandtransportwear

import android.app.Application

/**
 * Created by Edward Zhang on 15/11/2017.
 */

const val PREFS_NAME = "ATWearStorage"

class AucklandTransportWear : Application() {
    override fun onCreate() {
        super.onCreate()
        // Fires on launch...
        DataStore.sharedPrefs = getSharedPreferences(PREFS_NAME, 0)
        DataStore.readData()
    }
}