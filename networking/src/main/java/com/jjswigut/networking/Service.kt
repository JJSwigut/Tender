package com.jjswigut.networking

import com.jjswigut.data.BusinessList
import retrofit2.Response
import retrofit2.http.GET


interface Service {

    @GET("businesses/search?term=chinese&latitude=42.9790&longitude=-78.7923&radius=8000")
    suspend fun getBusinesses(): Response<BusinessList>


}
