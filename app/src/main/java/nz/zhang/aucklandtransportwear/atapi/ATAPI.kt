package nz.zhang.aucklandtransportwear.atapi

import android.util.Log
import com.google.gson.Gson
import nz.zhang.aucklandtransportwear.R
import nz.zhang.aucklandtransportwear.R.string.gtfs_key
import nz.zhang.aucklandtransportwear.atapi.listener.StopsListListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


const val ENDPOINT = "https://api.at.govt.nz/v2/"

class ATAPI {
    private val retrofitGTFS = Retrofit.Builder()
            .baseUrl(ENDPOINT)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GTFSService::class.java)

    fun getStopsGeo(lat: Double, long: Double, distance: Int, listener: StopsListListener) {
        val call = retrofitGTFS.stopsGeoSearch(ATConstants.gtfs_key, lat, long, distance)
        call.enqueue(object : Callback<ATResponse<List<Stop>>> {

            override fun onFailure(call: Call<ATResponse<List<Stop>>>?, t: Throwable?) {
                listener.update(null)
                Log.e("AT API", t?.message)
            }

            override fun onResponse(call: Call<ATResponse<List<Stop>>>?, response: Response<ATResponse<List<Stop>>>?) {
                val body: ATResponse<List<Stop>>? = response?.body()
                if (response != null && response?.isSuccessful && body != null && body.status == "OK") {
                    listener.update(body.response)
                } else {
                    listener.update(null)
                    Log.e("AT API", response?.errorBody().toString())
                }
            }
        })
    }
}