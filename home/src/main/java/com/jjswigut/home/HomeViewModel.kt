package com.jjswigut.home


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.core.utils.FirebaseUserLiveData
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.MatchingEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {

    private val _groupLiveData = MutableLiveData<List<Group>>()
    val groupLiveData: LiveData<List<Group>> get() = _groupLiveData

    private val _matchingEventLiveData = MutableLiveData<List<MatchingEvent>>()
    val matchingEventLiveData: LiveData<List<MatchingEvent>> get() = _matchingEventLiveData


    fun getListOfGroups() {
        repo.getAllGroups()
            .addOnSuccessListener { result ->
                val groupList = arrayListOf<Group>()
                for (document in result) {
                    groupList.add(document.toObject())
                }
                _groupLiveData.value = groupList
            }
    }

    fun getListOfMatchingEvents() {
        repo.getAllEvents()
            .addOnSuccessListener { result ->
                val eventList = arrayListOf<MatchingEvent>()
                for (document in result) {
                    eventList.add(document.toObject())
                }
                _matchingEventLiveData.value = eventList
            }
    }

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }

    companion object {
        const val groups = "groups"
        const val events = "events"
    }
}