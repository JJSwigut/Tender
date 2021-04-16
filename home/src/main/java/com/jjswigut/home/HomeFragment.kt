package com.jjswigut.home

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.jjswigut.core.base.BaseFragment
import com.jjswigut.core.utils.State
import com.jjswigut.home.databinding.FragmentHomeBinding
import com.jjswigut.home.presentation.adapters.EventListAdapter
import com.jjswigut.home.presentation.adapters.GroupListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                with(viewModel) {
                    if (isNewUser()) {
                        saveUserToFirestore()
                    }
                }
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (FirebaseAuth.getInstance().currentUser == null) {
            launchSignIn()
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
                HomeViewModel.AuthenticationState.UNAUTHENTICATED -> {
                    binding.welcomeHeader.text = "You look hungry stranger! Let's get you signed in"
                }
                else -> Log.d(TAG, "observeAuthentication: authState null")
            }
        })
    }


    private fun observeGroups() {
        viewModel.listOfUserGroups.observe(viewLifecycleOwner, { result ->
            when (result) {
                is State.Loading -> showLoadingView()
                is State.Success -> result.data?.let { groupAdapter.updateData(it) }
                is State.Failed -> Log.d(TAG, "observeGroups: ${result.message}")
            }
        })
    }

    private fun observeMatchingEvents() {
        viewModel.listOfUserEvents.observe(viewLifecycleOwner, { result ->
            when (result) {
                is State.Loading -> showLoadingView()
                is State.Success -> result.data?.let { eventAdapter.updateData(it) }
                is State.Failed -> Log.d(TAG, "observeMatchingEvents: ${result.message}")
            }

        })
    }

    private fun launchSignIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build()
        )

        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.LoginTheme)
                .setIsSmartLockEnabled(false)
                .build(),
            RC_SIGN_IN
        )
    }

    companion object {
        const val createGroup = 0
        const val RC_SIGN_IN = 9001
    }
}