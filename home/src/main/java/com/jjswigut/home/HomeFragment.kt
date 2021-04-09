package com.jjswigut.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.jjswigut.core.base.BaseFragment
import com.jjswigut.home.databinding.FragmentHomeBinding
import com.jjswigut.home.presentation.adapters.EventListAdapter
import com.jjswigut.home.presentation.adapters.GroupListAdapter

class HomeFragment : BaseFragment<HomeViewModel>() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override val viewModel: HomeViewModel by activityViewModels()

    private lateinit var groupAdapter: GroupListAdapter
    private lateinit var eventAdapter: EventListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupAdapter = GroupListAdapter()
        eventAdapter = EventListAdapter(viewModel)
        viewModel.getListOfGroups()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupRecyclers()
        binding.addGroupButton.setOnClickListener {
            createGroupDialog()
        }
        binding.addEventButton.setOnClickListener {
            createEventDialog()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    private fun createGroupDialog() {
        viewModel.navigate(
            HomeFragmentDirections.actionHomeFragmentToCreateGroupDialogFragment(
                createGroup, null
            )
        )
    }

    private fun createEventDialog() {
        viewModel.navigate(
            HomeFragmentDirections.actionHomeFragmentToEventDialogFragment()
        )
    }


    private fun setupRecyclers() {
        with(binding) {
            groupRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            groupRecyclerView.adapter = groupAdapter

            eventRecyclerView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            eventRecyclerView.adapter = eventAdapter
        }
    }

    private fun setupObservers() {
        observeAuthentication()
        observeGroups()
        observeMatchingEvents()
    }

    private fun observeAuthentication() {
        viewModel.authenticationState.observe(viewLifecycleOwner, { authState ->
            when (authState) {
                HomeViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.welcomeHeader.text = getString(
                        R.string.welcome_message,
                        FirebaseAuth.getInstance().currentUser?.displayName
                    )
                }
                else -> binding.welcomeHeader.text = getString(R.string.welcome_message, "friend")
            }
        })
    }


    private fun observeGroups() {
        viewModel.groupLiveData.observe(viewLifecycleOwner, { groups ->
            groups?.let { groupAdapter.updateData(it) }
        })
    }

    private fun observeMatchingEvents() {
        viewModel.matchingEventLiveData.observe(viewLifecycleOwner, { events ->
            events?.let { eventAdapter.updateData(it) }
        })
    }

    companion object {
        const val createGroup = 0
    }
}