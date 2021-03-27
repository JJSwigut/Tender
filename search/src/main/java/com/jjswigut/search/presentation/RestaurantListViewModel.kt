package com.jjswigut.search.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jjswigut.data.RestaurantRepository
import com.jjswigut.data.models.BusinessList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantListViewModel @Inject constructor(
    private val repo: RestaurantRepository
) : ViewModel() {


    val restaurantListLiveData = MutableLiveData<List<BusinessList.Businesses>>()

    val likedRestaurants = arrayListOf<BusinessList.Businesses>()

    fun getRestaurants(foodType: String, radius: Int, lat: Float, lon: Float) =
        viewModelScope.launch {
            repo.getRestaurants(foodType, radius, lat, lon).collect { businesses ->
                if (businesses != null) {
                    restaurantListLiveData.value = businesses.data?.businesses
                }
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
