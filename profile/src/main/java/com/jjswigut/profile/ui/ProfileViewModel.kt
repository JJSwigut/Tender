package com.jjswigut.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

import com.jjswigut.profile.FirebaseUserLiveData


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