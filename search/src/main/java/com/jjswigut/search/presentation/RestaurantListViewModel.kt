package com.jjswigut.search.presentation

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.core.utils.State
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.RestaurantRepository
import com.jjswigut.data.models.BusinessList
import com.jjswigut.data.models.Event
import com.jjswigut.data.models.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantListViewModel @Inject constructor(
    private val repo: RestaurantRepository,
    private val firestorerepo: FirestoreRepository
) : BaseViewModel() {


    val currentUser = firestorerepo.currentUserId

    private val _restaurantListLiveData = MutableLiveData<List<BusinessList.Businesses>>()
    val restaurantListLiveData: LiveData<List<BusinessList.Businesses>> get() = _restaurantListLiveData

    val likedRestaurants = arrayListOf<BusinessList.Businesses?>()

    var isEventStarted: Boolean = false

    var eventBeingBuilt = Event()

    var mGroup: Group? = null

    fun getRestaurants(foodType: String, radius: Int, lat: Float, lon: Float) =
        viewModelScope.launch {
            repo.getRestaurants(foodType, radius, lat, lon).collect { restaurants ->
                when (restaurants) {
                    is State.Loading -> Log.d(TAG, "getRestaurants: loading")
                    is State.Success -> _restaurantListLiveData.value =
                        restaurants.data.businesses!!
                    is State.Failed -> Log.d(TAG, "getRestaurants: ${restaurants.message}")
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
                "userList" to mGroup?.users,
                "foodType" to foodType,
                "restaurantList" to restaurantList,
                "userChoices" to userChoices
            )
            newEventRef.set(newEvent, SetOptions.merge())
        }
    }

    fun getGroupAndWriteEvent() {
        val groupRef = eventBeingBuilt.groupId?.let { firestorerepo.getGroup(it) }
        groupRef?.addOnSuccessListener { group ->
            mGroup = group.toObject()
            writeEventToUsersInGroup(mGroup, eventBeingBuilt)
        }
    }

    private fun writeEventToUsersInGroup(group: Group?, event: Event) {
        group?.users?.let {
            for (user in it) {
                user.userId?.let { userId ->
                    firestorerepo.getUserReference(userId)
                        .update("userEvents", FieldValue.arrayUnion(event))
                }
            }
        }
    }

    fun resetMemberVariables() {
        eventBeingBuilt = Event()
        mGroup = null
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
