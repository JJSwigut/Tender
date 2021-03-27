package com.jjswigut.data.remote

import com.jjswigut.core.utils.Resource
import com.jjswigut.data.models.BusinessList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val service: Service
) : BaseDataSource() {

    fun getSearchResults(
        foodType: String,
        radius: Int,
        lat: Float,
        lon: Float
    ): Flow<Resource<BusinessList>?> {
        return flow {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = getResult {
                    service.getBusinesses(
                        foodType,
                        radius,
                        lat,
                        lon
                    )
                }).data)
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))

            }
        }
    }

}