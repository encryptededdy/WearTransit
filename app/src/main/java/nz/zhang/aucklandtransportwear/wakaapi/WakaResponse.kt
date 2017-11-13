package nz.zhang.aucklandtransportwear.wakaapi

data class WakaResponse (private val currentTime: Int, val trips: ArrayList<WakaService>, val realtime: Map<String, WakaRealtime>) {
    fun updateTrips() {
        // delete anything that's already left
        val iterator = trips.iterator()
        while (iterator.hasNext()) {
            val trip = iterator.next()
            if (trip.departure_time_seconds - currentTime < 0) {
                iterator.remove()
            } else {
                trip.requestTime = currentTime
                val rt = realtime.get(trip.trip_id)
                if (rt != null) {
                    trip.isRealtime = true
                    trip.delay = rt.delay
                }
            }
        }
    }
}