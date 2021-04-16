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
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    val db: FirebaseFirestore,
    val auth: FirebaseAuth
) {


    val currentUserId get() = auth.currentUser?.uid
    val currentUser: FirebaseUser? get() = auth.currentUser
    val userCollection get() = db.collection(users)

    @ExperimentalCoroutinesApi
    suspend fun getUserGroups(): Flow<List<Group>?> = callbackFlow {

        val snapshot = db.collection(users).document(currentUserId!!)

        val subscription = snapshot.addSnapshotListener { _, _ ->
            snapshot.get().addOnSuccessListener {
                val user = it.toObject<User>()
                offer(user?.userGroups)
            }
        }
        awaitClose { subscription.remove() }

    }

    @ExperimentalCoroutinesApi
    suspend fun getUserEvents(): Flow<List<Event>?> = callbackFlow {

        val snapshot = db.collection(users).document(currentUserId!!)

        val subscription = snapshot.addSnapshotListener { _, _ ->
            snapshot.get().addOnSuccessListener {
                val user = it.toObject<User>()
                offer(user?.userEvents)
            }
        }
        awaitClose { subscription.remove() }

    }


    @ExperimentalCoroutinesApi
    suspend fun getAllUsers(): Flow<List<User>> = callbackFlow {
        val snapshot = db.collection(users)

        val subscription = snapshot.addSnapshotListener { users, _ ->
            val allUsers = arrayListOf<User>()
            for (doc in users!!.documents) {
                doc.toObject<User>()?.let { allUsers.add(it) }
            }
            offer(allUsers)
        }
        awaitClose { subscription.remove() }

    }

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