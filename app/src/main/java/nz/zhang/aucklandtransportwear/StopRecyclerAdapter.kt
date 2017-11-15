package nz.zhang.aucklandtransportwear

import android.content.Context
import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_stop.view.*
import nz.zhang.aucklandtransportwear.wakaapi.Stop
import nz.zhang.aucklandtransportwear.wakaapi.StopType

/**
 * Created by Edward Zhang on 13/11/2017.
 */
class StopRecyclerAdapter(context: Context, private val stops: List<Stop>) : RecyclerView.Adapter<StopRecyclerAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val context = parent?.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val rtServiceView = inflater.inflate(R.layout.item_stop, parent, false)
        // Return a new holder instance
        return ViewHolder(rtServiceView)
    }

    override fun getItemCount(): Int {
        return stops.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val stop = stops[position]
        holder?.stopName?.text = stop.stop_name
        holder?.stopID?.text = stop.stop_id
        when (stop.stopType()) {
            StopType.TRAIN -> holder?.stopIcon?.setImageDrawable(holder.stopIcon.context.getDrawable(R.drawable.ic_train_white_24dp))
            StopType.BUS -> holder?.stopIcon?.setImageDrawable(holder.stopIcon.context.getDrawable(R.drawable.ic_directions_bus_white_24dp))
            StopType.FERRY -> holder?.stopIcon?.setImageDrawable(holder.stopIcon.context.getDrawable(R.drawable.ic_directions_boat_white_24dp))
        }
        holder?.backLayout?.setOnClickListener {
            val stopIntent = Intent(holder?.backLayout?.context, StopActivity::class.java)
            stopIntent.putExtra("stop", stop)
            startActivity(holder?.backLayout?.context, stopIntent, null)
        }
    }

    init {
        setHasStableIds(true)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopIcon: ImageView = itemView.stopIcon
        val stopName: TextView = itemView.stopName
        val stopID: TextView = itemView.stopID
        val backLayout: ConstraintLayout = itemView.backLayout
    }
}