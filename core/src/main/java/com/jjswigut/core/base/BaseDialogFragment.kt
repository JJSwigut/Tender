package com.jjswigut.core.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.jjswigut.core.NavCommand

abstract class BaseDialogFragment<ViewModel : BaseViewModel> : DialogFragment() {

    protected abstract val viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navCommand.observe(viewLifecycleOwner, { command ->
            when (command) {
                is NavCommand.To -> findNavController().navigate(command.directions)
                is NavCommand.DeepLink -> findNavController().navigate(command.deepLink)
            }
        })
    }

    fun showLoadingView() {

    }
}
