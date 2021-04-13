package com.jjswigut.tender

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.jjswigut.data.models.User

class MainActivityViewModel : ViewModel() {
    var isSigningIn = false


    fun saveUserToFirestore(user: FirebaseUser?, db: FirebaseFirestore) {
        user?.let {
            val documentReference: DocumentReference =
                db.collection("users").document(user.uid)
            documentReference.set(
                User(
                    userId = user.uid,
                    name = user.displayName,
                    email = user.email,
                    profilePhotoUrl = user.photoUrl.toString()
                )
            )
        }
    }
}