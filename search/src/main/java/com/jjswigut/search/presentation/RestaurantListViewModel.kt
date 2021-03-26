package com.jjswigut.search.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.jjswigut.core.utils.Resource
import com.jjswigut.data.RestaurantRepository
import com.jjswigut.data.models.BusinessList
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class RestaurantListViewModel @Inject constructor(
    private val repo: RestaurantRepository
) : ViewModel() {


    val restaurantListLiveData = MutableLiveData<List<BusinessList.Businesses>>()

    val likedRestaurants = arrayListOf<BusinessList.Businesses>()

    fun getRestaurants(foodType: String, radius: Int, lat: Float, lon: Float) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = repo.getRestaurants(foodType, radius, lat, lon)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            }
        }

    fun saveLikedRestaurants(action: CardAction) {
        when (action) {
            is CardAction.CardSwiped -> {
                likedRestaurants.add(action.restaurant)
            }
        }
    }


}
