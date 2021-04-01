package com.jjswigut.home

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.data.models.Event
import com.jjswigut.data.models.Group
import com.jjswigut.home.presentation.adapters.EventListAdapter
import com.jjswigut.home.presentation.adapters.GroupListAdapter

class HomeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val groupList = arrayListOf<Group>()

    private val eventList = arrayListOf<Event>()

    fun getListOfGroups(adapter: GroupListAdapter) {
        db.collection("groups")
            .get()
            .addOnSuccessListener { result ->
                groupList.clear()
                for (document in result) {
                    groupList.add(document.toObject())
                }
                adapter.updateData(groupList)
            }
    }

    fun getListOfUsers(adapter: EventListAdapter) {
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                eventList.clear()
                for (document in result) {
                    eventList.add(document.toObject())
                }
                adapter.updateData(eventList)
            }
    }
}