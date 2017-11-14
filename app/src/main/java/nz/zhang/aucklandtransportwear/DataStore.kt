package nz.zhang.aucklandtransportwear

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import nz.zhang.aucklandtransportwear.wakaapi.Cities
import nz.zhang.aucklandtransportwear.wakaapi.Stop

/**
 * Created by Edward Zhang on 14/11/2017.
 */

const val STOPS_KEY = "saved_stops"
const val CITY_KEY = "selected_city"

object DataStore {
    var savedStops = ArrayList<Stop>()
    var selectedCity = Cities.AUCKLAND

    lateinit var sharedPrefs: SharedPreferences

    fun readData() {
        // Get stops
        val stopListType = object : TypeToken<ArrayList<Stop>>() {}.type
        val stops: ArrayList<Stop>? = Gson().fromJson(sharedPrefs.getString(STOPS_KEY, ""), stopListType)
        if (stops != null) {
            savedStops = stops
        }

        // Get city
        val city = Gson().fromJson(sharedPrefs.getString(CITY_KEY, ""), Cities::class.java)
        if (city != null) {
            selectedCity = city
        }
    }

    fun writeData() {
        val editor = sharedPrefs.edit()
        editor.putString(STOPS_KEY, Gson().toJson(savedStops))
        editor.putString(CITY_KEY, Gson().toJson(selectedCity))
        editor.apply()
    }
}