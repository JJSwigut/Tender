package com.jjswigut.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.data.models.Event
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    val db: FirebaseFirestore
) : FireStoreFlowWrapper() {

    val auth get() = FirebaseAuth.getInstance()
    val currentUserId get() = auth.currentUser?.uid
    val currentUser: FirebaseUser? get() = auth.currentUser
    val userCollection get() = db.collection(users)

    @ExperimentalCoroutinesApi
    val userGroups = listenToFireStore(db, Group::class, users, currentUserId!!)

    @ExperimentalCoroutinesApi
    val userEvents = listenToFireStore(db, Event::class, users, currentUserId!!)


    @ExperimentalCoroutinesApi
    val allUsers = listenToFireStore(db, users)

    fun createNewGroup(): DocumentReference {
        return db.collection(groups).document()
    }

    fun getGroup(groupId: String): Task<DocumentSnapshot> {
        return db.collection(groups).document(groupId)
            .get()
    }


    fun deleteGroup(groupId: String): Task<Void> {
        db.collection(users).document(currentUserId!!).get().addOnSuccessListener { snapshot ->
            val user = snapshot.toObject<User>()
            val userGroups = user?.userGroups
            userGroups?.removeIf { it.groupId == groupId }

            snapshot.reference.update("userGroups", userGroups)
        }
        return db.collection(groups).document(groupId)
            .delete()

    }

    fun getUserReference(userId: String): DocumentReference {
        return db.collection(users).document(userId)
    }

    fun createNewEvent(): DocumentReference {
        return db.collection(events).document()
    }


    fun getEventReference(eventId: String): DocumentReference {
        return db.collection(events).document(eventId)
    }


    companion object {
        const val groups = "groups"
        const val events = "events"
        const val users = "users"
    }
}