package com.jjswigut.tender

import com.google.firebase.firestore.DocumentReference
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.data.models.Event
import com.jjswigut.data.models.Group
import com.jjswigut.data.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {


    fun saveUserToFirestore() {
        repo.currentUser?.let {
            val documentReference: DocumentReference =
                repo.db.collection("users").document(it.uid)
            documentReference.set(
                User(
                    userId = it.uid,
                    name = it.displayName,
                    email = it.email,
                    profilePhotoUrl = it.photoUrl.toString(),
                    userEvents = arrayListOf<Event>(),
                    userGroups = arrayListOf<Group>()

                )
            )
        }
    }
}