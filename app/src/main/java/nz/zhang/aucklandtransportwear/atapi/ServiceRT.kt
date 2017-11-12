package nz.zhang.aucklandtransportwear.atapi

import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*

class ServiceRT(
        val scheduledArrivalTime: Date?,
        val scheduledDepartureTime: Date,
        val destinationDisplay: String,
        val expectedArrivalTime: Date?,
        val expectedDepartureTime: Date?,
        val route_short_name: String
) : Comparable<ServiceRT> {
    override fun compareTo(other: ServiceRT): Int {
        return departureTime().compareTo(other.departureTime())
    }

    fun departureTime() : LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(scheduledDepartureTime.time), ZoneId.systemDefault())
    }

    fun expectedArrivalTime() : LocalDateTime? {
        return if (expectedArrivalTime != null) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(expectedArrivalTime.time), ZoneId.systemDefault())
        } else {
            null
        }
    }

    fun expectedDepatureTime() : LocalDateTime? {
        return if (expectedDepartureTime != null) {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(expectedDepartureTime.time), ZoneId.systemDefault())
        } else {
            null
        }
    }
}