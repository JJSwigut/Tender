package com.jjswigut.profile.ui

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.jjswigut.profile.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {


    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels()


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
        bindInfoToProfile()
        binding.profilePic.setOnClickListener { openImagePicker() }
        binding.updateButton.setOnClickListener {
            updateUsername()
            updateEmail()
        }
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
                profileViewModel.currentImage = uri
            }
        currentUser?.let {
            profileViewModel.uploadProfilePicToStorage(
                requireContext(),
                "$it.profile",
                it,
                binding
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun toast(toast: String) {
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
        private val currentUser = FirebaseAuth.getInstance().currentUser
    }
}