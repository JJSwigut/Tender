package com.jjswigut.home.presentation

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.core.utils.State
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.models.Event
import com.jjswigut.data.models.Group
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class EventDialogViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {


    val groupSelection = MutableLiveData<String>().apply {
        value = "nobody"
    }
    val dateSelection = MutableLiveData<String>().apply {
        value = "the 1st of never"
    }

    var mGroup: Group? = null


    val listOfUserGroups = liveData(Dispatchers.IO) {
        emit(State.Loading)
        try {
            repo.userGroups.collect { groups ->
                emit(State.Success(groups))
            }
        } catch (exception: Exception) {
            emit(State.Failed(exception.toString()))
            Log.d(TAG, ":${exception.message} ")
        }
    }

    fun formatDate(date: Long): String {
        val formatter = SimpleDateFormat("EEE, MMM d, ''yy", Locale.US)
        return formatter.format(date)
    }

    fun saveEventWithGroupAndDate(eventId: String) {
        val eventRef = repo.getEventReference(eventId)
        val finishedEvent = hashMapOf(
            "groupId" to mGroup?.groupId,
            "groupName" to mGroup?.groupName,
            "date" to dateSelection.value
        )
        eventRef.set(finishedEvent, SetOptions.merge())
        eventRef.get().addOnSuccessListener { event ->
            event.toObject<Event>()?.let { getGroupAndWriteEvent(it) }
        }
    }

    private fun getGroupAndWriteEvent(event: Event) {
        val groupRef = mGroup?.groupId?.let { repo.getGroup(it) }
        groupRef?.addOnSuccessListener { group ->
            mGroup = group.toObject()
            writeEventToUsersInGroup(mGroup, event)
        }
    }

    private fun writeEventToUsersInGroup(group: Group?, event: Event) {
        group?.users?.let {
            for (user in it) {
                user.userId?.let { userId ->
                    repo.getUserReference(userId)
                        .update("userEvents", FieldValue.arrayUnion(event))
                }
            }
        }
    }


}
