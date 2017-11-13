package nz.zhang.aucklandtransportwear.wakaapi

import android.util.Log
import nz.zhang.aucklandtransportwear.atapi.Stop
import nz.zhang.aucklandtransportwear.wakaapi.listener.StopInfoListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val ENDPOINT = "https://getwaka.com/a/nz-akl/"

class WakaAPI {
    private val retrofitWaka = Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WakaService::class.java)

    fun getStopInfo(stop: Stop, listener: StopInfoListener) {
        val call = retrofitWaka.stopInfo(stop.stop_code)
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
}