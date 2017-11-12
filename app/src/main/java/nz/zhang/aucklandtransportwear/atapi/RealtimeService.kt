package nz.zhang.aucklandtransportwear.atapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RealtimeService {
    @GET("public-restricted/departures/{stopcode}")
    fun realtimeBoard(@Path("stopcode") stopCode: Int, @Query("subscription-key") subscriptionKey: String, @Query("hours") hours: Int) : Call<ATResponse<ATRTResponse>>
}