package com.capstone.skinaliyze.data.remote.retrofit

import com.capstone.skinaliyze.data.remote.response.SkincareResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("products")
    fun getSkincare(
        @Query("search") query: String = ""
    ): Call<SkincareResponse>
}