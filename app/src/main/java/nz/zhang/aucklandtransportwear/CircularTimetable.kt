package nz.zhang.aucklandtransportwear

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_circular_timetable.*
import kotlinx.android.synthetic.main.activity_stop.*
import nz.zhang.aucklandtransportwear.wakaapi.Stop
import nz.zhang.aucklandtransportwear.wakaapi.Trip
import nz.zhang.aucklandtransportwear.wakaapi.WakaAPI
import nz.zhang.aucklandtransportwear.wakaapi.listener.StopInfoListener
import com.google.android.gms.maps.CameraUpdateFactory.scrollBy
import android.support.wearable.input.RotaryEncoder
import android.util.Log
import android.view.InputDevice
import java.lang.Math.abs
import java.sql.Time
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
import java.util.concurrent.TimeUnit


class CircularTimetable : WearableActivity() {

    lateinit var stop : Stop
    private val displayedTrips : ArrayList<Trip> = ArrayList()
    private var currentlyDisplayed = 0
    private var encoderChange = 0f
    private var minute = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stop = intent.getParcelableExtra("stop")
        setContentView(R.layout.activity_circular_timetable)
        getRTData()
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        if (RotaryEncoder.isFromRotaryEncoder(event)) {
            if (event?.action == MotionEvent.ACTION_SCROLL) {
                encoderChange += RotaryEncoder.getRotaryAxisValue(event)
                if (encoderChange < 0 && abs(encoderChange) > 1) {
                    nextTrip()
                    encoderChange = 0f
                } else if (abs(encoderChange) > 1) {
                    previousTrip()
                    encoderChange = 0f
                }
                return true
            }
        }
        return super.onGenericMotionEvent(event)
    }

    private fun getRTData() {
        loading.visibility = View.VISIBLE
        WakaAPI().getStopInfo(stop, object: StopInfoListener {
            override fun update(trips: List<Trip>?) {
                if (trips != null) {
                    System.out.println("Populating trips... (${trips.size}")
                    loading.visibility = View.GONE
                    if (trips.isEmpty()) {
                        // Deal with if there are no trips returned by the server
                    } else {
                        drawTimetable(trips)
                    }
                }
            }

        })
    }

    private fun nextTrip() {
        currentlyDisplayed++
        if (currentlyDisplayed >= displayedTrips.size) {
            // oops we've gone over the end
            currentlyDisplayed = 0
        }
        showTripData(displayedTrips[currentlyDisplayed])
    }

    private fun previousTrip() {
        if (currentlyDisplayed != 0) {
            currentlyDisplayed--
        } else {
            currentlyDisplayed = displayedTrips.size - 1
        }
        showTripData(displayedTrips[currentlyDisplayed])
    }

    @SuppressLint("SetTextI18n")
    private fun showTripData(trip: Trip) {
        drawSelection(trip)
        timeText.text = "in ${TimeUnit.SECONDS.toMinutes(trip.departure_time_seconds - trip.requestTime.toLong())} min"
        tripNameText.text = trip.route_long_name
        shortCodeText.text = trip.route_short_name
    }

    private fun drawSelection(trip:Trip) {
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        val radius = width/2

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

        val startAngle = 270 + (trip.departure_time_seconds - trip.requestTime)/10 + minute*6
        paint.color = Color.parseColor(trip.route_color.padEnd(7, '0'))
        canvas.drawArc(0f, 0f, width.toFloat(), height.toFloat(), startAngle.toFloat()-1, 9.5f, true, paint)

        paint.color = Color.parseColor("#3F51B5")
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radius.toFloat()-40, paint)

        popoutImage.setImageBitmap(bitmap)
        drawMinutesHand()
    }

    private fun drawTimetable(trips : List<Trip>) {
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        val radius = width/2

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.RED
        paint.isAntiAlias = true

        //canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radius.toFloat(), paint)
        //canvas.drawArc(0f, 0f, width.toFloat(), height.toFloat(), 270f, 45f, true, paint)

        minute = Calendar.getInstance().get(Calendar.MINUTE)

        clockText.text = ""

        displayedTrips.clear()
        trips.forEach {
            if (it.departure_time_seconds - it.requestTime < 3540) {
                // Service is in the next hour and should be displayed
                // TODO: Deal with overlapping services
                displayedTrips.add(it)
                val startAngle = 270 + (it.departure_time_seconds - it.requestTime)/10 + minute*6
                paint.color = Color.parseColor(it.route_color.padEnd(7, '0'))
                canvas.drawArc(0f, 0f, width.toFloat(), height.toFloat(), startAngle.toFloat(), 7.5f, true, paint)
            }
        }

        showTripData(displayedTrips[currentlyDisplayed])

        // Draw the inner circle thing
        paint.color = Color.parseColor("#3F51B5")
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radius.toFloat()-25, paint)

        chartImage.setImageBitmap(bitmap)
    }

    private fun drawMinutesHand() {
        val width = Resources.getSystem().displayMetrics.widthPixels
        val height = Resources.getSystem().displayMetrics.heightPixels
        val radius = width/2

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.isAntiAlias = true

        // Draw the minutes hand
        canvas.drawArc(0f, 0f, width.toFloat(), height.toFloat(), minute*6 + 270.toFloat(), -2.5f, true, paint)

        // Draw the inner inner circle thing
        paint.color = Color.parseColor("#3F51B5")
        canvas.drawCircle((width/2).toFloat(), (height/2).toFloat(), radius.toFloat()-50, paint)

        minutesHandImage.setImageBitmap(bitmap)

        // Update the onscreen clock
        val df = SimpleDateFormat("h:mm a")
        clockText.text = df.format(Calendar.getInstance().time)
    }
}
