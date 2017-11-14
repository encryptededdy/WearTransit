package nz.zhang.aucklandtransportwear

import nz.zhang.aucklandtransportwear.wakaapi.Cities
import nz.zhang.aucklandtransportwear.wakaapi.Stop

/**
 * Created by Edward Zhang on 14/11/2017.
 */
object DataStore {
    val savedStops = ArrayList<Stop>()
    var selectedCity = Cities.AUCKLAND
}