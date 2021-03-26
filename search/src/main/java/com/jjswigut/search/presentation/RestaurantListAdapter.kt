package com.jjswigut.search.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jjswigut.core.utils.ListDiffCallback
import com.jjswigut.data.models.BusinessList
import com.jjswigut.search.databinding.ItemRestaurantBinding
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction

class RestaurantListAdapter(
    private val swipeHandler: CardSwipeHandler,
    private val viewModel: RestaurantListViewModel,
    context: Context
) : RecyclerView.Adapter<RestaurantListAdapter.ViewHolder>(), CardStackListener {

    val cardManager = CardStackLayoutManager(context, this)

    private val elements: ArrayList<BusinessList.Businesses> = arrayListOf()

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

    fun updateData(newData: List<BusinessList.Businesses>) {

        val diffResult = DiffUtil.calculateDiff(
            ListDiffCallback(newList = newData, oldList = elements)
        )
        elements.clear()
        elements.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(
        private val binding: ItemRestaurantBinding,
        private val elements: List<BusinessList.Businesses>
    ) : RecyclerView.ViewHolder(binding.root) {

        private val imageView = binding.cardImage
        private val nameView = binding.restaurantName
        private val ratingView = binding.rating
        private val priceView = binding.price
        private fun element() = elements[adapterPosition]


        fun bind(item: BusinessList.Businesses) {
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
                    cardManager.topPosition,
                    elements[cardManager.topPosition]
                )
            )
        }
    }

    override fun onCardRewound() {
        viewModel.likedRestaurants.remove(elements[cardManager.topPosition])
    }

    override fun onCardCanceled() {}
    override fun onCardAppeared(view: View?, position: Int) {}
    override fun onCardDisappeared(view: View?, position: Int) {}
}


typealias CardSwipeHandler = (CardAction.CardSwiped) -> Unit