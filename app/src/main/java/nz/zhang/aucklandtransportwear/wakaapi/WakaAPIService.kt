package nz.zhang.aucklandtransportwear.wakaapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WakaAPIService {
    @GET("station/{stopcode}/times")
    fun stopInfo(@Path("stopcode") stopCode: String) : Call<WakaResponse>

    @GET("station/search")
    fun stopGeoSearch(@Query("lat") latitude: Double, @Query("lon") longitude: Double, @Query("distance") distance: Int) : Call<List<Stop>>
}