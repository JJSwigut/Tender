package com.jjswigut.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.jjswigut.search.databinding.FragmentRestaurantlistBinding
import com.jjswigut.search.presentation.CardAction
import com.jjswigut.search.presentation.RestaurantListAdapter
import com.jjswigut.search.presentation.RestaurantListViewModel
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeableMethod
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RestaurantListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: RestaurantListViewModel by viewModels { viewModelFactory }

    private lateinit var listAdapter: RestaurantListAdapter
    private val args: RestaurantListFragmentArgs by navArgs()

    private var _binding: FragmentRestaurantlistBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter = RestaurantListAdapter(::handleSwipe, viewModel, requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRestaurantlistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCardStackView()
        getRestaurants()
        observeRestaurants()

        binding.rewindButton.setOnClickListener {
            binding.cardStack.rewind()

        }


    }

    private fun handleSwipe(action: CardAction) {
        viewModel.saveLikedRestaurants(action)
    }

    private fun handleRewind() {

    }


    private fun setupCardStackView() {
        listAdapter.cardManager.setStackFrom(StackFrom.Top)
        listAdapter.cardManager.setVisibleCount(5)
        listAdapter.cardManager.setTranslationInterval(12.0f)
        listAdapter.cardManager.setScaleInterval(0.85f)
        listAdapter.cardManager.setSwipeThreshold(0.3f)
        listAdapter.cardManager.setMaxDegree(20.0f)
        listAdapter.cardManager.setDirections(Direction.HORIZONTAL)
        listAdapter.cardManager.setCanScrollHorizontal(true)
        listAdapter.cardManager.setCanScrollVertical(true)
        listAdapter.cardManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        binding.cardStack.adapter = listAdapter
        binding.cardStack.layoutManager = listAdapter.cardManager

    }


    private fun getRestaurants() {
        viewModel.getRestaurants(args.foodType, args.radius, args.lat, args.lon)
            .observe(viewLifecycleOwner, {
                if (!it.data.isNullOrEmpty()) {
                    viewModel.restaurantListLiveData.value = it.data
                    val list = it.data
                    if (list != null) {
                        this.listAdapter.updateData(list)
                    }

                }
            })
    }

    private fun observeRestaurants() {
        viewModel.restaurantListLiveData.observe(viewLifecycleOwner, {
            listAdapter.updateData(it)
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

