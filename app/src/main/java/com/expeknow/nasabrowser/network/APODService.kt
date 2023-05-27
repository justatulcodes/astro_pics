package com.expeknow.nasabrowser.network

import com.expeknow.nasabrowser.models.APODModel
import com.expeknow.nasabrowser.models.APODMultiModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.Date

interface APODService {

    @GET("apod")
    fun getTodayAPOD(@Query("api_key")api_key: String)
    : Call<APODModel>

    @GET("apod")
    fun getMultipleAPOD(@Query("api_key")api_key: String, @Query("count")count: Int)
    : Call<APODMultiModel>

    /**
     * Date in format  YYYY-MM-DD. Minimum date : 1995-06-16 | Maximum : Today
     */
    @GET("apod")
    fun getDatedAPOD(@Query("api_key")api_key: String, @Query("date")date:  String)
    : Call<APODModel>
}