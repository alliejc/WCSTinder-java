package com.alliejc.wcstinder.trackmyswing

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by acaldwell on 4/8/18.
 */
interface APIInterface {
    @GET("{division}/{role}")
    fun getDancers(@Path("division")division:String, @Path("role")role:String): Call<MutableList<Dancer>>


}