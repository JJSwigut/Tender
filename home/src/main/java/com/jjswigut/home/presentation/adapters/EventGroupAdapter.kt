package com.jjswigut.home.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jjswigut.core.utils.ListDiffCallback
import com.jjswigut.data.models.Group
import com.jjswigut.home.databinding.GroupCardBinding
import com.jjswigut.home.presentation.EventDialogViewModel

class EventGroupAdapter(
    private val viewModel: EventDialogViewModel
) : RecyclerView.Adapter<EventGroupAdapter.ViewHolder>() {


    private val elements: ArrayList<Group> = arrayListOf()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            binding = GroupCardBinding.inflate(
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

    fun updateData(newData: List<Group>) {

        val diffResult = DiffUtil.calculateDiff(
            ListDiffCallback(newList = newData, oldList = elements)
        )
        elements.clear()
        elements.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(
        private val binding: GroupCardBinding,
        private val elements: List<Group>
    ) : RecyclerView.ViewHolder(binding.root) {

        private val groupCard = binding.groupCard
        private val groupNameView = binding.groupNameView
        private fun element() = elements[adapterPosition]

        fun bind(item: Group) {
            groupNameView.text = element().groupName
            groupCard.setOnClickListener {
                viewModel.group = element()
                viewModel.groupSelection.value = element().groupName
            }
        }
    }

}
