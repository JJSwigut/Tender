package com.jjswigut.home.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jjswigut.core.utils.ListDiffCallback
import com.jjswigut.data.models.Event
import com.jjswigut.home.HomeViewModel
import com.jjswigut.home.databinding.EventCardBinding

class EventListAdapter(
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<EventListAdapter.ViewHolder>() {

    private val elements: ArrayList<Event> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            binding = EventCardBinding.inflate(
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

    fun updateData(newData: List<Event>) {

        val diffResult = DiffUtil.calculateDiff(
            ListDiffCallback(newList = newData, oldList = elements)
        )
        elements.clear()
        elements.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(
        private val binding: EventCardBinding,
        private val elements: List<Event>
    ) : RecyclerView.ViewHolder(binding.root) {

        private val foodTypeView = binding.foodTypeView
        private val groupNameView = binding.groupNameView
        private val dateView = binding.dateView
        private fun element() = elements[adapterPosition]


        fun bind(item: Event) {
            foodTypeView.text = element().foodType
            groupNameView.text = element().groupName
            dateView.text = element().date.toString()


        }

    }
}
