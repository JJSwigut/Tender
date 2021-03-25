package com.jjswigut.search.presentation

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import com.jjswigut.search.ui.SearchFragmentDirections

class SearchViewModel : ViewModel() {


    val searchRadius = MutableLiveData<Int>().apply {
        value = 1
    }

    val foodSelection = MutableLiveData<String>().apply {
        value = "Chinese"
    }

    fun radiusInMeters(radius: MutableLiveData<Int>): Int? {
        return radius.value?.times(1609)
    }

    fun navigateToCardStackWithQueryParams(
        view: View,
        lat: Float,
        lon: Float
    ) {
        val foodType = foodSelection.value
        val radius = radiusInMeters(searchRadius)
        view.findNavController()
            .navigate(
                SearchFragmentDirections.actionSearchFragmentToRestaurantListFragment(
                    foodType!!, radius!!, lat, lon
                )
            )

    }

}