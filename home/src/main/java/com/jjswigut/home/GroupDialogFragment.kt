package com.jjswigut.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.jjswigut.core.R
import com.jjswigut.core.base.BaseDialogFragment
import com.jjswigut.core.utils.State
import com.jjswigut.home.databinding.FragmentGroupDialogueBinding
import com.jjswigut.home.presentation.GroupDialogViewModel
import com.jjswigut.home.presentation.adapters.GroupAdapter

class GroupDialogFragment : BaseDialogFragment<GroupDialogViewModel>() {

    override val viewModel: GroupDialogViewModel by activityViewModels()

    private var _binding: FragmentGroupDialogueBinding? = null
    private val binding get() = _binding!!

    private val args: GroupDialogFragmentArgs by navArgs()

    private lateinit var adapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialogStyle)
        adapter = GroupAdapter(viewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGroupDialogueBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        discernFragmentUse()



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler() {
        with(binding) {
            createGroupRecycler.layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            createGroupRecycler.adapter = adapter
        }
    }

    private fun discernFragmentUse() {
        if (args.dialogType == createGroup) {
            viewModel.isModifyDialog = false
            setupToCreateGroup()
        } else if (args.dialogType == modifyGroup) {
            viewModel.isModifyDialog = true
            setupToModifyGroup()
        }
    }

    private fun setupToCreateGroup() {
        observeAllUsers()
        with(binding) {
            deleteGroupButton.visibility = View.GONE
            viewModel.newGroupUserList.clear()
            createGroupButton.setOnClickListener {
                viewModel.createNewGroup(groupNameInputTextView.text.toString())
                viewModel.navigate(GroupDialogFragmentDirections.actionCreateGroupDialogFragmentToHomeFragment())
            }
        }
    }

    private fun setupToModifyGroup() {
        args.groupId?.let { viewModel.getGroupToModify(adapter, it, binding) }
        with(binding) {
            createGroupButton.text =
                getString(com.jjswigut.home.R.string.update_group_button)
            createGroupButton.setOnClickListener {
                viewModel.updateGroup(groupNameInputTextView.text.toString())
                viewModel.navigate(GroupDialogFragmentDirections.actionCreateGroupDialogFragmentToHomeFragment())
            }
            deleteGroupButton.visibility = View.VISIBLE
            deleteGroupButton.setOnClickListener {
                viewModel.deleteGroup(requireContext())
                viewModel.navigate(GroupDialogFragmentDirections.actionCreateGroupDialogFragmentToHomeFragment())
            }
        }
    }

    private fun observeAllUsers() {
        viewModel.listOfAllUsers.observe(viewLifecycleOwner, { result ->
            when (result) {
                is State.Loading -> showLoadingView()
                is State.Success -> result.data?.let { adapter.updateData(it) }
                is State.Failed -> Toast.makeText(
                    requireContext(),
                    "Something went wrong! Try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    companion object {
        const val createGroup = 0
        const val modifyGroup = 1
    }

}