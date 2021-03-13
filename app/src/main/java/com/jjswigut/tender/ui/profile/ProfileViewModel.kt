package com.jjswigut.tender.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.jjswigut.tender.ui.FirebaseUserLiveData

class ProfileViewModel : ViewModel() {

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED
    }

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}