package nz.zhang.aucklandtransportwear.wakaapi

import android.os.Parcel
import android.os.Parcelable

class Stop (
        val stop_id: String,
        val stop_name: String,
        val stop_lat: Double,
        val stop_lon: Double,
        val stop_region: String,
        val route_type: Int
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        return if (other is Stop) {
            other.stop_id == stop_id
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return stop_id.hashCode()
    }

    fun stopType(): StopType {
        return when (route_type) {
            2 -> StopType.TRAIN
            4 -> StopType.FERRY
            else -> StopType.BUS // TODO: Find out from Jono what type 1 is
        }
    }

    override fun describeContents(): Int {
        return hashCode()
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        p0?.writeString(stop_id)
        p0?.writeString(stop_name)
        p0?.writeDouble(stop_lat)
        p0?.writeDouble(stop_lon)
        p0?.writeString(stop_region)
        p0?.writeInt(route_type)
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(), parcel.readString(), parcel.readDouble(), parcel.readDouble(),
            parcel.readString(), parcel.readInt())

    companion object CREATOR : Parcelable.Creator<Stop> {
        override fun createFromParcel(parcel: Parcel): Stop {
            return Stop(parcel)
        }

        override fun newArray(size: Int): Array<Stop?> {
            return arrayOfNulls(size)
        }
    }

}