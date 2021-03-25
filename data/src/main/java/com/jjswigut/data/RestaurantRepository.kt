package com.jjswigut.data

import com.jjswigut.data.models.BusinessList
import com.jjswigut.data.remote.RemoteDataSource
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    suspend fun getRestaurants(
        foodType: String,
        radius: Int,
        lat: Float,
        lon: Float
    ): List<BusinessList.Businesses>? {
        return remoteDataSource.getSearchResults(foodType, radius, lat, lon).data?.businesses
    }
}