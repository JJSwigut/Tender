package com.jjswigut.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.jjswigut.core.R
import com.jjswigut.core.base.BaseDialogFragment
import com.jjswigut.core.utils.State
import com.jjswigut.data.models.Group
import com.jjswigut.home.databinding.FragmentEventDialogBinding
import com.jjswigut.home.presentation.EventDialogViewModel
import com.jjswigut.home.presentation.adapters.EventGroupAdapter

class EventDialogFragment : BaseDialogFragment<EventDialogViewModel>() {

    override val viewModel: EventDialogViewModel by activityViewModels()

    private var _binding: FragmentEventDialogBinding? = null
    private val binding get() = _binding!!

    private val args: EventDialogFragmentArgs by navArgs()

    private val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
    private val picker: MaterialDatePicker<*> = builder.build()


    private lateinit var adapter: EventGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialogStyle)
        adapter = EventGroupAdapter(viewModel)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        discernFragmentUse()
        setupObservers()
        setupRecycler()
        setupDatePicker()
        binding.chooseDateButton.setOnClickListener {
            picker.show(parentFragmentManager, picker.toString())
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecycler() {
        binding.createEventRecycler.layoutManager =
            GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
        binding.createEventRecycler.adapter = adapter
    }

    private fun setupObservers() {
        observeGroups()
        observeDateSelection()
        observeGroupSelection()
    }

    private fun observeGroups() {
        viewModel.listOfUserGroups.observe(viewLifecycleOwner, { result ->
            when (result) {
                is State.Loading -> showLoadingView()
                is State.Success -> adapter.updateData(result.data!! as List<Group>)
                is State.Failed -> Toast.makeText(
                    requireContext(),
                    "Something went wrong! Try again",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun observeGroupSelection() {
        viewModel.groupSelection.observe(viewLifecycleOwner, {
            binding.eatWithView.text = getString(com.jjswigut.home.R.string.lets_eat_with, it)
        })
    }

    private fun observeDateSelection() {
        viewModel.dateSelection.observe(viewLifecycleOwner, {
            binding.onDateView.text = getString(com.jjswigut.home.R.string.on_date, it)
        })
    }

    private fun setupDatePicker() {
        picker.addOnPositiveButtonClickListener {
            viewModel.dateSelection.value = viewModel.formatDate(it as Long)
        }
    }

    private fun discernFragmentUse() {
        if (args.eventId == notCreatedYet) {
            setupForNewEvent()
        } else setupToFinishEvent()
    }

    private fun setupForNewEvent() {
        with(binding) {
            createEventButton.text = getString(com.jjswigut.home.R.string.create_event_button)
            createEventButton.setOnClickListener {
                if (groupAndDatePicked()) {
                    with(viewModel) {
                        val uri = Uri.parse(
                            "Tender://SearchFragment?" +
                                    "groupName=${groupSelection.value}&" +
                                    "groupId=${mGroup?.groupId}&" +
                                    "date=${dateSelection.value}"
                        )
                        navigate(uri)
                    }
                }
            }
        }
    }

    private fun setupToFinishEvent() {
        with(binding) {
            createEventButton.text = getString(com.jjswigut.home.R.string.save_event_button)
            createEventButton.setOnClickListener {
                if (groupAndDatePicked()) {
                    with(viewModel) {
                        saveEventWithGroupAndDate(args.eventId)
                        navigate(EventDialogFragmentDirections.actionEventDialogFragmentToHomeFragment())
                    }
                }
            }
        }
    }

    private fun groupAndDatePicked(): Boolean {
        with(viewModel) {
            if (groupSelection.value == "nobody" || dateSelection.value == "the 1st of never") {
                Toast.makeText(
                    requireContext(),
                    "Choose a date and group to proceed!",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            } else return true
        }
    }

    companion object {
        const val notCreatedYet = "0"
    }
}