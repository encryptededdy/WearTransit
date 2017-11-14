package nz.zhang.aucklandtransportwear.wakaapi

import android.util.Log
import nz.zhang.aucklandtransportwear.DataStore
import nz.zhang.aucklandtransportwear.wakaapi.Stop
import nz.zhang.aucklandtransportwear.wakaapi.listener.StopInfoListener
import nz.zhang.aucklandtransportwear.wakaapi.listener.StopSearchListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WakaAPI {

    private val endpoint = "https://getwaka.com/a/${DataStore.selectedCity.shortCode}/"

    private val retrofitWaka = Retrofit.Builder()
            .baseUrl(endpoint)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WakaAPIService::class.java)

    fun getStopInfo(stop: Stop, listener: StopInfoListener) {
        val call = retrofitWaka.stopInfo(stop.stop_id)
        call.enqueue(object : Callback<WakaResponse> {
            override fun onResponse(call: Call<WakaResponse>?, response: Response<WakaResponse>?) {
                val body: WakaResponse? = response?.body()
                if (body != null) {
                    body.updateTrips()
                    listener.update(body.trips)
                } else {
                    listener.update(null)
                    Log.e("Waka API", response?.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<WakaResponse>?, t: Throwable?) {
                listener.update(null)
                Log.e("Waka API", t?.message)
            }

        })
    }

    fun searchStopsGeo(latitude: Double, longitude: Double, distance: Int, listener: StopSearchListener) {
        val call = retrofitWaka.stopGeoSearch(latitude, longitude, distance)
        call.enqueue(object : Callback<List<Stop>> {
            override fun onResponse(call: Call<List<Stop>>?, response: Response<List<Stop>>?) {
                val body: List<Stop>? = response?.body()
                if (body != null) {
                    listener.update(body)
                } else {
                    listener.update(null)
                    Log.e("Waka API", response?.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<List<Stop>>?, t: Throwable?) {
                listener.update(null)
                Log.e("Waka API", t?.message)
            }

        })
    }
}