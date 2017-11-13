package nz.zhang.aucklandtransportwear.wakaapi

import nz.zhang.aucklandtransportwear.atapi.ServiceRT

/**
 * Created by Edward Zhang on 13/11/2017.
 */
data class WakaTrip (
        val trip_id: String,
        val route_short_name: String,
        val route_long_name: String,
        val departure_time_seconds: Int,
        val arrival_time_seconds: Int
) : Comparable<WakaTrip> {

    var isRealtime = false
    var delay = 0
    var requestTime = 0

    override fun compareTo(other: WakaTrip): Int {
        return (departure_time_seconds + delay).compareTo(other.departure_time_seconds + other.delay)
    }
}