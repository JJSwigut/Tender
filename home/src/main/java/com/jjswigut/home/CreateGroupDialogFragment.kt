package com.jjswigut.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.jjswigut.core.R
import com.jjswigut.home.databinding.FragmentCreateGroupDialogueBinding
import com.jjswigut.home.presentation.CreateGroupDialogViewModel
import com.jjswigut.home.presentation.adapters.CreateGroupAdapter

class CreateGroupDialogFragment : DialogFragment() {

    private val viewModel: CreateGroupDialogViewModel by activityViewModels()

    private var _binding: FragmentCreateGroupDialogueBinding? = null
    private val binding get() = _binding!!

    private val args: CreateGroupDialogFragmentArgs by navArgs()

    private lateinit var adapter: CreateGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialogStyle)
        adapter = CreateGroupAdapter(viewModel)

        if (args.dialogType == createGroup) {
            viewModel.getListOfAllUsers(adapter)
        } else if (args.dialogType == modifyGroup) {
            args.groupId?.let { viewModel.getUsersInGroup(adapter, it) }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGroupDialogueBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        viewModel.newGroupUserList.clear()
        binding.createGroupButton.setOnClickListener {
            viewModel.createNewGroup(binding.groupNameInputTextView.text.toString())
            parentFragment?.findNavController()
                ?.navigate(CreateGroupDialogFragmentDirections.actionCreateGroupDialogFragmentToHomeFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler() {
        binding.createGroupRecycler.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.createGroupRecycler.adapter = adapter
    }

    companion object {
        const val createGroup = 0
        const val modifyGroup = 1
    }

}