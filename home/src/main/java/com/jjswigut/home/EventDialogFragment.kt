package com.jjswigut.home

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.jjswigut.core.R
import com.jjswigut.core.base.BaseDialogFragment
import com.jjswigut.home.databinding.FragmentEventDialogBinding
import com.jjswigut.home.presentation.EventDialogViewModel
import com.jjswigut.home.presentation.adapters.EventGroupAdapter

class EventDialogFragment : BaseDialogFragment<EventDialogViewModel>() {

    override val viewModel: EventDialogViewModel by activityViewModels()

    private var _binding: FragmentEventDialogBinding? = null
    private val binding get() = _binding!!

    private val builder: MaterialDatePicker.Builder<*> = MaterialDatePicker.Builder.datePicker()
    private val picker: MaterialDatePicker<*> = builder.build()


    private lateinit var adapter: EventGroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.dialogStyle)
        adapter = EventGroupAdapter(viewModel)
        viewModel.getListOfGroups(adapter)


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
        setupObservers()
        setupRecycler()
        setupDatePicker()
        binding.chooseDateButton.setOnClickListener {
            picker.show(parentFragmentManager, picker.toString())
        }
        binding.createEventButton.setOnClickListener {
            with(viewModel) {
                val uri = Uri.parse(
                    "Tender://SearchFragment?" +
                            "groupName=${groupSelection.value}&" +
                            "groupId=${group?.groupId}&" +
                            "date=${dateSelection.value}"
                )
                findNavController().navigate(uri)
            }
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
        observeDateSelection()
        observeGroupSelection()
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


}