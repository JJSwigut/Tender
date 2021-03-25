package com.jjswigut.data.remote

import com.jjswigut.core.utils.Resource
import com.jjswigut.data.models.BusinessList
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val service: Service
) : BaseDataSource() {

    suspend fun getSearchResults(
        foodType: String,
        radius: Int,
        lat: Float,
        lon: Float
    ): Resource<BusinessList> {
        return getResult { service.getBusinesses(foodType, radius, lat, lon) }
    }
}