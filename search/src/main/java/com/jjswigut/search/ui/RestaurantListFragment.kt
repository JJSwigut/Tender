package com.jjswigut.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.jjswigut.core.base.BaseFragment
import com.jjswigut.search.databinding.FragmentRestaurantlistBinding
import com.jjswigut.search.presentation.CardAction
import com.jjswigut.search.presentation.RestaurantListAdapter
import com.jjswigut.search.presentation.RestaurantListViewModel
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import com.yuyakaido.android.cardstackview.SwipeableMethod
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RestaurantListFragment : BaseFragment<RestaurantListViewModel>() {

    override val viewModel: RestaurantListViewModel by activityViewModels()

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
        startBuildingEvent()
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


    private fun setupCardStackView() {
        with(listAdapter.cardManager) {
            setStackFrom(StackFrom.Top)
            setVisibleCount(10)
            setTranslationInterval(12.0f)
            setScaleInterval(0.85f)
            setSwipeThreshold(0.3f)
            setMaxDegree(30.0f)
            setDirections(Direction.HORIZONTAL)
            setCanScrollHorizontal(true)
            setCanScrollVertical(true)
            setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        }
        with(binding.cardStack) {
            adapter = listAdapter
            layoutManager = listAdapter.cardManager
        }
    }


    private fun getRestaurants() {
        viewModel.getRestaurants(args.foodType, args.radius, args.lat, args.lon)
    }

    private fun observeRestaurants() {
        viewModel.restaurantListLiveData.observe(viewLifecycleOwner, { businessList ->
            businessList?.let { listAdapter.updateData(businessList) }
        })
    }

    private fun startBuildingEvent() {
        if (args.groupId != "0") {
            viewModel.isEventStarted = true
            with(viewModel.eventBeingBuilt) {
                groupId = args.groupId
                groupName = args.groupName
                date = args.date
                foodType = args.foodType
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

