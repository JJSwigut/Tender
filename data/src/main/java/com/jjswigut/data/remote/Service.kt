package com.jjswigut.data.remote

import com.jjswigut.data.BuildConfig.yelpKey
import com.jjswigut.data.models.BusinessList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Service {

    @GET("businesses/search?")
    suspend fun getBusinesses(
        @Query("term") foodType: String,
        @Query("radius") radius: Int,
        @Query("latitude") lat: Float,
        @Query("longitude") lon: Float,
        @Header("Authorization") authHeader: String = "Bearer $yelpKey "
    ): Response<BusinessList>
}
