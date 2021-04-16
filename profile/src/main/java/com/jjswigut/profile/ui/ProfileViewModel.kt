package com.jjswigut.profile.ui

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import coil.load
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jjswigut.core.base.BaseViewModel
import com.jjswigut.data.FirestoreRepository
import com.jjswigut.profile.databinding.FragmentProfileBinding
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: FirestoreRepository
) : BaseViewModel() {


    var currentImage: Uri? = null
    private val imageRef = Firebase.storage.reference
    private val db = repo.db
    val currentUser get() = repo.currentUser


    fun uploadProfilePicToStorage(
        context: Context,
        filename: String,
        currentUser: FirebaseUser,
        binding: FragmentProfileBinding
    ) =
        viewModelScope.launch {
            try {
                currentImage?.let { imageUri ->
                    imageRef.child("images/$filename").putFile(imageUri)
                        .addOnSuccessListener { upload ->
                            val result = upload.metadata?.reference?.downloadUrl
                            result?.addOnSuccessListener { downloadUrl ->
                                val profileUpdate = userProfileChangeRequest {
                                    photoUri = downloadUrl
                                }
                                currentUser.updateProfile(profileUpdate)

                                val photoData =
                                    hashMapOf("profilePhotoUrl" to downloadUrl.toString())
                                currentUser.let {
                                    db.collection("users").document(currentUser.uid).set(
                                        photoData,
                                        SetOptions.merge()
                                    )
                                }
                            }
                        }
                    withContext(Dispatchers.Main) {
                        toast(context, "You've successfully changed your profile picture!")
                        binding.profilePic.load(currentImage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.message?.let { toast(context, it) }
                }

            }
        }

    private fun toast(context: Context, toast: String) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }
}