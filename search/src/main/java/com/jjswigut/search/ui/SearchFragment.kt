package com.jjswigut.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.slider.Slider
import com.jjswigut.core.base.BaseFragment
import com.jjswigut.search.R
import com.jjswigut.search.databinding.FragmentSearchBinding
import com.jjswigut.search.presentation.LocationHelper
import com.jjswigut.search.presentation.SearchViewModel

class SearchFragment : BaseFragment<SearchViewModel>() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override val viewModel: SearchViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val args: SearchFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        locationHelper.getLocationPermission(requireActivity())
        lifecycleScope.launchWhenResumed {
            locationHelper.getLastLocation(
                fusedLocationClient,
                requireActivity()
            )
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            if (!args.groupName.equals("nobody")) {
                groupAndDateView.visibility = View.VISIBLE
                groupAndDateView.text =
                    getString(R.string.group_date_string, args.groupName, args.date)
            }

            chineseButton.setOnClickListener {
                viewModel.foodSelection.value = getString(R.string.chinese)
            }
            italianButton.setOnClickListener {
                viewModel.foodSelection.value = getString(R.string.italian)
            }
            bbqButton.setOnClickListener {
                viewModel.foodSelection.value = getString(R.string.bbq)
            }
            americanButton.setOnClickListener {
                viewModel.foodSelection.value = getString(R.string.american)
            }
            coffeeButton.setOnClickListener {
                viewModel.foodSelection.value = getString(R.string.coffee)
            }
            deliButton.setOnClickListener {
                viewModel.foodSelection.value = getString(R.string.deli)
            }
            binding.breakfastButton.setOnClickListener {
                viewModel.foodSelection.value = getString(R.string.breakfast)
            }
            searchButton.setOnClickListener {
                if (locationHelper.userLocation != null) {
                    if (args.groupId == "0") {
                        viewModel.navigateToCardStackWithoutEvent(
                            locationHelper.userLocation!!.latitude.toFloat(),
                            locationHelper.userLocation!!.longitude.toFloat()
                        )
                    } else {
                        viewModel.navigateToCardStackWithEvent(
                            locationHelper.userLocation!!.latitude.toFloat(),
                            locationHelper.userLocation!!.longitude.toFloat(),
                            args.groupName!!,
                            args.groupId!!,
                            args.date!!
                        )
                    }
                } else Toast.makeText(requireContext(), "No location!", Toast.LENGTH_LONG)
                    .show()
            }
            radiusSelector.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
                viewModel.searchRadius.value = slider.value.toInt()
            })
        }
        viewModel.searchRadius.observe(viewLifecycleOwner,
            { binding.radius.text = getString(R.string.radius, it) })

        viewModel.foodSelection.observe(viewLifecycleOwner, { binding.foodType.text = it })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    companion object {
        private val locationHelper = LocationHelper()
    }
}

