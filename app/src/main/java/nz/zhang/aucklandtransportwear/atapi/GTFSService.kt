package nz.zhang.aucklandtransportwear.atapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GTFSService {
    @GET("gtfs/stops/geosearch")
    fun stopsGeoSearch(@Header("Ocp-Apim-Subscription-Key")apiKey:String, @Query("lat")lat:Double, @Query("lng")lng:Double, @Query("distance")dist:Int) : Call<ATResponse<List<Stop>>>
}