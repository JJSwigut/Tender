package com.jjswigut.home


import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.core.utils.FirebaseUserLiveData
import com.jjswigut.core.utils.State
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.models.Event
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {


    val listOfUserGroups = liveData(Dispatchers.IO) {
        emit(State.Loading)
        try {
            repo.userGroups.collect { groups ->
                emit(State.Success(groups))
            }
        } catch (exception: Exception) {
            emit(State.Failed(exception.toString()))
            Log.d(TAG, ":fuck ${exception.message} ")
        }
    }

    val listOfUserEvents = liveData(Dispatchers.IO) {
        emit(State.Loading)
        try {
            repo.userEvents.collect { events ->
                emit(State.Success(events))
            }
        } catch (exception: Exception) {
            emit(State.Failed(exception.toString()))
            Log.d(TAG, ":fuck ${exception.message} ")
        }
    }

    fun isNewUser(): Boolean {
        var isNewUser = true
        repo.currentUserId?.let {
            repo.userCollection.document(it).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    isNewUser = false
                }
            }
        }
        return isNewUser
    }

    fun saveUserToFirestore() {
        repo.currentUser?.let { user ->
            repo.userCollection.document(user.uid).set(
                User(
                    userId = user.uid,
                    name = user.displayName,
                    email = user.email,
                    profilePhotoUrl = user.photoUrl?.toString(),
                    userEvents = arrayListOf<Event>(),
                    userGroups = arrayListOf<Group>()
                )
            )
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

}
