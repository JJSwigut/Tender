package com.jjswigut.search.presentation

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jjswigut.core.utils.ListDiffCallback
import com.jjswigut.data.models.MinimalRestaurant
import com.jjswigut.search.databinding.ItemRestaurantBinding
import com.jjswigut.search.ui.RestaurantListFragmentDirections
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

class
RestaurantListAdapter(
    private val swipeHandler: CardSwipeHandler,
    private val viewModel: RestaurantListViewModel,
    private val context: Context
) : RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>(), CardStackListener {

    val cardManager = CardStackLayoutManager(context, this)

    private val elements: ArrayList<MinimalRestaurant> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            binding = ItemRestaurantBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            elements = elements
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = elements[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = elements.size

    fun updateData(newData: List<MinimalRestaurant>) {

        val diffResult = DiffUtil.calculateDiff(
            ListDiffCallback(newList = newData, oldList = elements)
        )
        elements.clear()
        elements.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(
        private val binding: ItemRestaurantBinding,
        private val elements: List<MinimalRestaurant>
    ) : RecyclerView.ViewHolder(binding.root) {

        private val imageView = binding.cardImage
        private val nameView = binding.restaurantName
        private val ratingView = binding.rating
        private val priceView = binding.price
        private fun element() = elements[adapterPosition]


        fun bind(item: MinimalRestaurant) {
            imageView.load(element().imageUrl)
            nameView.text = element().name
            ratingView.text = "Rating: ${element().rating}"
            priceView.text = "Price: ${element().price}"

        }
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardSwiped(direction: Direction?) {
        if (direction == Direction.Right) {
            swipeHandler(
                CardAction.CardSwiped(
                    cardManager.topPosition - 1,
                    elements[cardManager.topPosition - 1]
                )
            )
        }
        if (cardManager.topPosition - 1 == elements.size - 1) {
            with(AlertDialog.Builder(context)) {
                setMessage("Would you like to share these results with the Group?")
                setNegativeButton(
                    "Nah"
                ) { _, _ ->
                    viewModel.isEventStarted = false
                    viewModel.navigate(RestaurantListFragmentDirections.actionRestaurantListFragmentToSearchFragment())
                }
                setPositiveButton("Sure!") { _, _ ->
                    with(viewModel) {
                        eventBeingBuilt.restaurantList = elements
                        eventBeingBuilt.userChoices =
                            arrayListOf(hashMapOf(currentUser to likedRestaurants))
                        createEventInFirestore()
                        if (isEventStarted) {
                            getGroupAndWriteEvent()
                            navigateToHomeFragment()
                        } else navigateToEventDialog()
                    }
                }
                create()
                show()
            }
        }
    }

    override fun onCardRewound() {
        swipeHandler(
            CardAction.CardRewound(
                cardManager.topPosition, elements[cardManager.topPosition]
            )
        )
        Log.d(TAG, "onCardRewound: ${elements[cardManager.topPosition]}")
    }

    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}
}



typealias CardSwipeHandler = (CardAction) -> Unit
