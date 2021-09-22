package nz.zhang.aucklandtransportwear

import android.content.Context
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.item_rtservice.view.*
import nz.zhang.aucklandtransportwear.wakaapi.Trip
import java.util.concurrent.TimeUnit

/**
 * Created by Edward Zhang on 13/11/2017.
 */
class TripRecyclerAdapter(private val context: Context, private val trips: List<Trip>) : RecyclerView.Adapter<TripRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val rtServiceView = inflater.inflate(R.layout.item_rtservice, parent, false)
        // Return a new holder instance
        return ViewHolder(rtServiceView)
    }

    override fun getItemCount(): Int {
        return trips.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = trips[position]
        if (service.trip_headsign.isBlank()) {
            holder.destinationShort.text = service.route_long_name
        } else {
            holder.destinationShort.text = service.trip_headsign
        }
        holder.shortName.text = service.route_short_name
        holder.destination.text = service.route_long_name
        holder.serviceColour.setBackgroundColor(Color.parseColor(service.route_color.padEnd(7, '0')))
        if (service.trip_headsign.isNotBlank()) {
            // Don't offer the expanded view if there isn't one...
            holder.backLayout.setOnClickListener {
                if (holder.destination.visibility == View.VISIBLE) {
                    holder.destination.visibility = View.GONE
                    //holder.destination_short.visibility = View.VISIBLE
                } else {
                    holder.destination.visibility = View.VISIBLE
                    //holder.destination_short.visibility = View.GONE
                }
            }
        }
        if (service.isRealtime) {
            System.out.println("Using live tracking")
            // we have live tracking & we know when the bus is coming
            holder.liveIcon.visibility = View.VISIBLE
            holder.liveIcon.setImageResource(R.drawable.ic_gps_fixed_white_24dp)
        }

        if ((service.departure_time_seconds - service.requestTime + service.delay) > 0) {
            // Hasn't arrived yet
            holder.eta.text = formatTime(service.departure_time_seconds - service.requestTime + service.delay)
        } else {
            holder.eta.text = context.getString(R.string.service_time_due)
        }
    }

    private fun formatTime(seconds:Int) : String {
        return if (TimeUnit.SECONDS.toMinutes(seconds.toLong()) < 60) {
            "${TimeUnit.SECONDS.toMinutes(seconds.toLong())}m"
        } else {
            "${TimeUnit.SECONDS.toHours(seconds.toLong())}h"
        }
    }

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shortName: TextView = itemView.shortName
        val destination: TextView = itemView.destn
        val destinationShort: TextView = itemView.destnShort
        val eta: TextView = itemView.eta
        val liveIcon: ImageView = itemView.liveIcon
        val backLayout: ConstraintLayout = itemView.backLayout
        val serviceColour: LinearLayout = itemView.serviceColour
    }
}