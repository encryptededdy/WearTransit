package nz.zhang.aucklandtransportwear

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_rtservice.view.*
import nz.zhang.aucklandtransportwear.atapi.ServiceRT
import org.threeten.bp.Instant
import java.util.concurrent.TimeUnit

/**
 * Created by Edward Zhang on 13/11/2017.
 */
class ServiceRTAdapter (context: Context, private val services: List<ServiceRT>) : RecyclerView.Adapter<ServiceRTAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val context = parent?.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val rtServiceView = inflater.inflate(R.layout.item_rtservice, parent, false)
        // Return a new holder instance
        return ViewHolder(rtServiceView)
    }

    override fun getItemCount(): Int {
        return services.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val service = services[position]
        holder?.shortName?.text = service.route_short_name
        holder?.destination?.text = service.destinationDisplay
        if (service.expectedArrivalTime != null) {
            // we have live tracking & we know when the bus is coming
            holder?.liveIcon?.visibility = View.VISIBLE
            if ((service.expectedArrivalTime.time/1000) - Instant.now().epochSecond > 0) {
                // Hasn't arrived yet
                holder?.eta?.text = formatTime((service.expectedArrivalTime.time/1000) - Instant.now().epochSecond)
            } else if ((service.expectedArrivalTime.time/1000) - Instant.now().epochSecond > -60) {
                // Arrived <1m ago
                holder?.eta?.text = "NOW"
            }
        } else {
            // no live tracking, use departure time
            holder?.liveIcon?.visibility = View.INVISIBLE
            if ((service.scheduledDepartureTime.time/1000) - Instant.now().epochSecond > 0) {
                // Hasn't arrived yet
                holder?.eta?.text = formatTime((service.scheduledDepartureTime.time/1000) - Instant.now().epochSecond)
            }
        }
    }

    private fun formatTime(seconds:Long) : String {
        return if (TimeUnit.SECONDS.toMinutes(seconds) < 100) {
            "${TimeUnit.SECONDS.toMinutes(seconds)}m"
        } else {
            "${TimeUnit.SECONDS.toHours(seconds)}h"
        }
    }

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shortName = itemView.shortName
        val destination = itemView.destn
        val eta = itemView.eta
        val liveIcon = itemView.liveIcon
    }
}