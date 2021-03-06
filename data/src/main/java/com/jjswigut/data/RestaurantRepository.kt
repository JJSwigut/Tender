package com.jjswigut.data

import com.jjswigut.core.utils.Resource
import com.jjswigut.data.models.BusinessList
import com.jjswigut.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    fun getRestaurants(
        foodType: String,
        radius: Int,
        lat: Float,
        lon: Float
    ): Flow<Resource<BusinessList>?> {
        return remoteDataSource.getSearchResults(foodType, radius, lat, lon)
    }
}