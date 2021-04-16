package com.jjswigut.search.presentation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.search.ui.SearchFragmentDirections
import java.util.*

class SearchViewModel : BaseViewModel() {


    val searchRadius = MutableLiveData<Int>().apply {
        value = 1
    }

    val foodSelection = MutableLiveData<String>().apply {
        value = "Chinese"
    }

    private fun radiusInMeters(radius: MutableLiveData<Int>): Int? {
        return radius.value?.times(1609)
    }

    fun navigateToCardStackWithoutEvent(
        lat: Float,
        lon: Float
    ) {
        val foodType = formatFoodTypeForSearch()
        Log.d(TAG, "navigateToCardStackWithoutEvent: $foodType")
        val radius = radiusInMeters(searchRadius)
        navigate(
            SearchFragmentDirections.actionSearchFragmentToRestaurantListFragment(
                foodType, radius!!, lat, lon
            )
        )

    }

    fun navigateToCardStackWithEvent(
        lat: Float,
        lon: Float,
        groupName: String,
        groupId: String,
        date: String
    ) {
        val foodType = formatFoodTypeForSearch()
        val radius = radiusInMeters(searchRadius)
        navigate(
            SearchFragmentDirections.actionSearchFragmentToRestaurantListFragment(
                foodType, radius!!, lat, lon, groupName, groupId, date
            )
        )

    }

    private fun formatFoodTypeForSearch(): String {
        return foodSelection.value!!.filter { !it.isWhitespace() }.toLowerCase(Locale.ROOT)
    }

}