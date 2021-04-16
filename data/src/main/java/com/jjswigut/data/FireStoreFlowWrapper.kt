package com.jjswigut.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.*
import kotlin.reflect.KClass


abstract class FireStoreFlowWrapper {
    @ExperimentalCoroutinesApi
    fun listenToFireStore(
        database: FirebaseFirestore,
        collection: String
    ): Flow<ArrayList<User>?> = callbackFlow {
        val snapshot = database.collection(collection)

        val subscription = snapshot.addSnapshotListener { users, _ ->
            val allUsers = arrayListOf<User>()

            users!!.documents.listIterator().forEach { snapshot ->
                snapshot.toObject<User>()?.let { user -> allUsers.add(user) }
            }
            offer(allUsers)
        }
        awaitClose { subscription.remove() }

    }

    @ExperimentalCoroutinesApi
    fun <T : Any> listenToFireStore(
        database: FirebaseFirestore,
        clazz: KClass<T>,
        collection: String,
        document: String
    ): Flow<ArrayList<out Any>?> = callbackFlow {

        val snapshot = database.collection(collection).document(document)

        val subscription = snapshot.addSnapshotListener { _, _ ->
            snapshot.get().addOnSuccessListener {
                val user = it.toObject<User>()

                when (clazz) {
                    User::class -> offer(user?.userEvents)
                    Group::class -> offer(user?.userGroups)
                }
            }
        }
        awaitClose { subscription.remove() }

    }


    sealed class SnapShot<out T> {
        object DocumentSnapshot : SnapShot<Nothing>()

    }
}


