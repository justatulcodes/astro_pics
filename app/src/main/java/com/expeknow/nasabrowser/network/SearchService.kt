package com.expeknow.nasabrowser.network


import com.expeknow.nasabrowser.models.SearchModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("search")
    fun getSearchedImage(@Query("q")q : String, @Query("media_type") media_type :String = "image")
    : Call<SearchModel>

}