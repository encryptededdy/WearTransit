package nz.zhang.aucklandtransportwear.wakaapi

data class WakaService(
        val trip_id: String,
        val trip_headsign: String,
        val route_short_name: String,
        val route_long_name: String,
        val departure_time_seconds: Int,
        val arrival_time_seconds: Int
) : Comparable<WakaService> {

    var isRealtime = false
    var delay = 0
    var requestTime = 0

    override fun compareTo(other: WakaService): Int {
        return (departure_time_seconds + delay).compareTo(other.departure_time_seconds + other.delay)
    }
}