package com.jjswigut.profile.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import coil.load

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.jjswigut.profile.databinding.FragmentProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()

    private var currentImage: Uri? = null
    private val imageRef = Firebase.storage.reference
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
        binding.profilePic.setOnClickListener { openImagePicker() }
        binding.updateButton.setOnClickListener {
            updateUsername()
            updateEmail()
        }
    }

    private fun observeAuthenticationState() {
        profileViewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
            when (authState) {
                ProfileViewModel.AuthenticationState.AUTHENTICATED -> {
                    bindInfoToProfile()
                }
                else -> {
                    toast("Something didn't work right, try signing out and signing back in.")
                }
            }
        })
    }

    private fun bindInfoToProfile() {
        binding.usernameTextview.text =
            currentUser?.displayName?.toEditable()
        binding.emailTextview.text =
            currentUser?.email?.toEditable()
        binding.profilePic.load(currentUser?.photoUrl)
    }

    private fun updateEmail() {
        currentUser?.updateEmail(binding.emailTextview.text.toString())?.addOnCompleteListener {
            if (it.isSuccessful) {
                toast("You're Email is Updated!")
            } else toast("Email was not updated.")
        }
    }

    private fun updateUsername() {
        val newName = userProfileChangeRequest {
            displayName = binding.usernameTextview.text.toString()
        }
        currentUser?.updateProfile(newName)?.addOnCompleteListener {
            if (it.isSuccessful) {
                toast("Username was updated!")
            } else toast("Username was not updated.")
        }
    }

    private fun uploadProfilePicToStorage(filename: String) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                currentImage?.let { imageUri ->
                    imageRef.child("images/$filename").putFile(imageUri)
                        .addOnSuccessListener { upload ->
                            val result = upload.metadata?.reference?.downloadUrl
                            result?.addOnSuccessListener { downloadUrl ->
                                val profileUpdate = userProfileChangeRequest {
                                    photoUri = downloadUrl
                                }
                                currentUser?.updateProfile(profileUpdate)

                                val photoData =
                                    hashMapOf("profilePhotoUrl" to downloadUrl.toString())
                                currentUser?.let {
                                    db.collection("users").document(currentUser.uid).set(
                                        photoData,
                                        SetOptions.merge()
                                    )
                                }
                            }
                        }
                    withContext(Dispatchers.Main) {
                        toast("You've successfully changed your profile picture!")
                        binding.profilePic.load(currentImage)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    e.message?.let { toast(it) }
                }
            }
        }

    private fun openImagePicker() {
        Intent(Intent.ACTION_GET_CONTENT).also {
            it.type = "image/*"
            startActivityForResult(it, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK)
            data?.data?.let { uri ->
                currentImage = uri
            }
        uploadProfilePicToStorage("$currentUser.profile")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toast(toast: String) {
        Toast.makeText(requireContext(), toast, Toast.LENGTH_SHORT).show()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private val currentUser = FirebaseAuth.getInstance().currentUser
    }
}