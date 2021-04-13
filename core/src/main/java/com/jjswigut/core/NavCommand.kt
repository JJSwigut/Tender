package com.jjswigut.core

import android.net.Uri
import androidx.navigation.NavDirections

sealed class NavCommand {
    data class To(val directions: NavDirections) : NavCommand()
    data class DeepLink(val deepLink: Uri) : NavCommand()

}
