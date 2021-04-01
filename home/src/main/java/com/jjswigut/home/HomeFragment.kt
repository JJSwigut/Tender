package com.jjswigut.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.jjswigut.home.databinding.FragmentHomeBinding
import com.jjswigut.home.presentation.adapters.EventListAdapter
import com.jjswigut.home.presentation.adapters.GroupListAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels()

    private lateinit var groupAdapter: GroupListAdapter
    private lateinit var eventAdapter: EventListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupAdapter = GroupListAdapter(homeViewModel)
        eventAdapter = EventListAdapter(homeViewModel)
        homeViewModel.getListOfGroups(groupAdapter)
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
        setupRecyclers()
        binding.addGroupButton.setOnClickListener {
            createGroupDialogue(view)
        }
        binding.welcomeHeader.text = getString(R.string.welcome_message, currentUser?.displayName)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun createGroupDialogue(view: View) {
        view.findNavController().navigate(
            HomeFragmentDirections.actionHomeFragmentToCreateGroupDialogFragment(
                createGroup, null
            )
        )
    }

    private fun setupRecyclers() {
        binding.groupRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.groupRecyclerView.adapter = groupAdapter

        binding.eventRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.eventRecyclerView.adapter = eventAdapter

    }

    companion object {
        private val currentUser = FirebaseAuth.getInstance().currentUser
        const val createGroup = 0
    }
}