package nz.zhang.aucklandtransportwear.atapi

import java.util.*

class ServiceRT(
        val scheduledArrivalTime: Date,
        val scheduledDepartureTime: Date,
        val destinationDisplay: String,
        val expectedArrivalTime: Date,
        val expectedDepartureTime: Date,
        val route_short_name: String
)