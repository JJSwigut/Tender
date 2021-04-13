package com.jjswigut.core.base

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.jjswigut.core.NavCommand
import com.jjswigut.core.utils.SingleLiveEvent


abstract class BaseViewModel : ViewModel() {

    val navCommand = SingleLiveEvent<NavCommand>()

    fun navigate(directions: NavDirections) {
        navCommand.postValue(NavCommand.To(directions))
    }

    fun navigate(deepLink: Uri) {
        navCommand.postValue(NavCommand.DeepLink(deepLink))
    }
}