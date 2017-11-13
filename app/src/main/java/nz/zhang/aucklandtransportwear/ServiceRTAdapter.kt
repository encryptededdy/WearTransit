package nz.zhang.aucklandtransportwear

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        System.out.println("adding: ${service.route_short_name}")
        if (service.expectedDepartureTime != null) {
            System.out.println("Using live tracking")
            // we have live tracking & we know when the bus is coming
            holder?.liveIcon?.visibility = View.VISIBLE
            holder?.liveIcon?.setImageResource(R.drawable.ic_gps_fixed_white_24dp)
            if ((service.expectedDepartureTime.time/1000) - Instant.now().epochSecond > 0) {
                // Hasn't arrived yet
                holder?.eta?.text = formatTime((service.expectedDepartureTime.time/1000) - Instant.now().epochSecond)
            } else if ((service.expectedDepartureTime.time/1000) - Instant.now().epochSecond > -60) {
                // Departed
                holder?.eta?.text = "-1m"
            }
        } else {
            // no live tracking, use departure time
            //holder?.liveIcon?.visibility = View.INVISIBLE
            if ((service.scheduledDepartureTime.time/1000) - Instant.now().epochSecond > 0) {
                // Hasn't arrived yet
                holder?.eta?.text = formatTime((service.scheduledDepartureTime.time/1000) - Instant.now().epochSecond)
            }
        }
    }

    private fun formatTime(seconds:Long) : String {
        return if (TimeUnit.SECONDS.toMinutes(seconds) < 60) {
            "${TimeUnit.SECONDS.toMinutes(seconds)}m"
        } else {
            "${TimeUnit.SECONDS.toHours(seconds)}h"
        }
    }

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shortName: TextView = itemView.shortName
        val destination: TextView = itemView.destn
        val eta: TextView = itemView.eta
        val liveIcon: ImageView = itemView.liveIcon
    }
}