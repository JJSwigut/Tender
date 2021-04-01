package com.jjswigut.home.presentation

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.User
import com.jjswigut.home.presentation.adapters.CreateGroupAdapter

class CreateGroupDialogViewModel : ViewModel() {


    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val userList = arrayListOf<User>()

    val newGroupUserList = arrayListOf<User>()

    private val groupUserList = arrayListOf<User>()

    private var group: Group? = null

    fun getUsersInGroup(adapter: CreateGroupAdapter, groupId: String) {
        db.collection("groups").document(groupId)
            .get()
            .addOnSuccessListener { result ->
                group = result.toObject()
                for (user in group?.users!!) {
                    groupUserList.add(user)
                }
                adapter.updateData(groupUserList)
            }
    }

    fun getListOfAllUsers(adapter: CreateGroupAdapter) {
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                userList.clear()
                for (document in result) {
                    userList.add(document.toObject())
                }
                adapter.updateData(userList)
            }
    }


    fun createNewGroup(name: String) {

        val newGroupRef = db.collection("groups").document()
        val newGroup = hashMapOf(
            "groupId" to newGroupRef.id,
            "groupName" to name,
            "users" to newGroupUserList
        )
        newGroupRef.set(newGroup, SetOptions.merge())
    }
}