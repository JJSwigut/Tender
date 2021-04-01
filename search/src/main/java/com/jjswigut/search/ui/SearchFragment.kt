package com.jjswigut.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.slider.Slider
import com.jjswigut.search.R
import com.jjswigut.search.databinding.FragmentSearchBinding
import com.jjswigut.search.presentation.LocationHelper
import com.jjswigut.search.presentation.SearchViewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SearchViewModel by activityViewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        binding.chineseButton.setOnClickListener {
            searchViewModel.foodSelection.value = getString(R.string.chinese)
        }
        binding.italianButton.setOnClickListener {
            searchViewModel.foodSelection.value = getString(R.string.italian)
        }
        binding.bbqButton.setOnClickListener {
            searchViewModel.foodSelection.value = getString(R.string.bbq)
        }
        binding.americanButton.setOnClickListener {
            searchViewModel.foodSelection.value = getString(R.string.american)
        }
        binding.coffeeButton.setOnClickListener {
            searchViewModel.foodSelection.value = getString(R.string.coffee)
        }
        binding.deliButton.setOnClickListener {
            searchViewModel.foodSelection.value = getString(R.string.deli)
        }
        binding.breakfastButton.setOnClickListener {
            searchViewModel.foodSelection.value = getString(R.string.breakfast)
        }

        binding.radiusSelector.addOnChangeListener(Slider.OnChangeListener { slider, value, fromUser ->
            searchViewModel.searchRadius.value = slider.value.toInt()
        })

        searchViewModel.searchRadius.observe(viewLifecycleOwner,
            { binding.radius.text = getString(R.string.radius, it) })

        searchViewModel.foodSelection.observe(viewLifecycleOwner, { binding.foodType.text = it })


        binding.searchButton.setOnClickListener {
            if (locationHelper.userLocation != null) {
                searchViewModel.navigateToCardStackWithQueryParams(
                    view,
                    locationHelper.userLocation!!.latitude.toFloat(),
                    locationHelper.userLocation!!.longitude.toFloat()
                )
            } else Toast.makeText(requireContext(), "No location Picked", Toast.LENGTH_LONG).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    companion object {
        private val locationHelper = LocationHelper()
    }
}

