package com.jjswigut.data.remote

import com.jjswigut.core.utils.State
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
    ): Flow<State<BusinessList>?> {
        return flow {
            emit(State.Loading)
            try {
                emit(getResult {
                    service.getBusinesses(
                        foodType,
                        radius,
                        lat,
                        lon
                    )
                })
            } catch (exception: Exception) {
                emit(State.Failed(exception.message.toString()))

            }
        }
    }

}