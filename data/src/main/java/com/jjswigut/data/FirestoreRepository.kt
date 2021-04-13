package com.jjswigut.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun createNewGroup(): DocumentReference {
        return db.collection(groups).document()
    }

    fun getGroup(groupId: String): Task<DocumentSnapshot> {
        return db.collection(groups).document(groupId)
            .get()
    }

    fun getAllGroups(): Task<QuerySnapshot> {
        return db.collection(groups)
            .get()
    }

    fun deleteGroup(groupId: String): Task<Void> {
        return db.collection(groups).document(groupId)
            .delete()
    }

    fun getUser(userId: String): Task<DocumentSnapshot> {
        return db.collection(users).document(userId)
            .get()
    }

    fun getAllUsers(): Task<QuerySnapshot> {
        return db.collection(users)
            .get()
    }

    fun createNewEvent(): DocumentReference {
        return db.collection(events).document()
    }

    fun getAllEvents(): Task<QuerySnapshot> {
        return db.collection(events)
            .get()
    }

    companion object {
        const val groups = "groups"
        const val events = "events"
        const val users = "users"
    }
}