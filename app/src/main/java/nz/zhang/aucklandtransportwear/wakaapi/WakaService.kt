package nz.zhang.aucklandtransportwear.wakaapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface WakaService {
    @GET("station/{stopcode}/times")
    fun stopInfo(@Path("stopcode") stopCode: Int) : Call<WakaResponse>
}