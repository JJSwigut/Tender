package com.jjswigut.search.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.SetOptions
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.RestaurantRepository
import com.jjswigut.data.models.BusinessList
import com.jjswigut.data.models.MatchingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantListViewModel @Inject constructor(
    private val repo: RestaurantRepository,
    private val firestorerepo: FirestoreRepository
) : BaseViewModel() {


    val currentUser = firestorerepo.getCurrentUserId()

    private val _restaurantListLiveData = MutableLiveData<List<BusinessList.Businesses>>()
    val restaurantListLiveData: LiveData<List<BusinessList.Businesses>> get() = _restaurantListLiveData

    val likedRestaurants = arrayListOf<BusinessList.Businesses>()

    var isEventStarted: Boolean = false

    val eventBeingBuilt = MatchingEvent()

    fun getRestaurants(foodType: String, radius: Int, lat: Float, lon: Float) =
        viewModelScope.launch {
            repo.getRestaurants(foodType, radius, lat, lon).collect { businesses ->
                if (businesses != null) {
                    _restaurantListLiveData.value = businesses.data?.businesses
                }
            }
        }

    fun saveLikedRestaurants(action: CardAction) {
        when (action) {
            is CardAction.CardSwiped -> {
                likedRestaurants.add(action.restaurant)
            }
            is CardAction.CardRewound -> {
                likedRestaurants.remove(action.restaurant)
            }
        }
    }

    fun createEventInFirestore() {
        with(eventBeingBuilt) {
            val newEventRef = firestorerepo.createNewEvent()
            eventBeingBuilt.eventId = newEventRef.id
            val newEvent = hashMapOf(
                "eventId" to eventId,
                "groupId" to groupId,
                "groupName" to groupName,
                "date" to date,
                "foodType" to foodType,
                "restaurantList" to restaurantList,
                "userChoices" to userChoices
            )
            newEventRef.set(newEvent, SetOptions.merge())
        }
    }

    fun navigateToEventDialog() {
        val uri = Uri.parse("Tender://EventDialogFragment?eventId=${eventBeingBuilt.eventId}")
        navigate(uri)
    }

    fun navigateToHomeFragment() {
        val uri = Uri.parse("Tender://HomeFragment")
        navigate(uri)
    }


}
