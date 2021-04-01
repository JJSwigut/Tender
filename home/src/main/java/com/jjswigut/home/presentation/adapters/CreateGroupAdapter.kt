package com.jjswigut.home.presentation.adapters

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.jjswigut.core.utils.ListDiffCallback
import com.jjswigut.data.models.User
import com.jjswigut.home.databinding.UserItemBinding
import com.jjswigut.home.presentation.CreateGroupDialogViewModel

class CreateGroupAdapter(private val viewModel: CreateGroupDialogViewModel) :
    RecyclerView.Adapter<CreateGroupAdapter.ViewHolder>() {


    private val elements: ArrayList<User> = arrayListOf()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            binding = UserItemBinding.inflate(
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

    fun updateData(newData: List<User>) {

        val diffResult = DiffUtil.calculateDiff(
            ListDiffCallback(newList = newData, oldList = elements)
        )
        elements.clear()
        elements.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    inner class ViewHolder(
        private val binding: UserItemBinding,
        private val elements: List<User>
    ) : RecyclerView.ViewHolder(binding.root) {

        private val cardView = binding.userCardView
        private val imageView = binding.userProfilePhoto
        private val nameView = binding.userNameView
        private fun element() = elements[adapterPosition]


        fun bind(item: User) {
            imageView.load(element().profilePhotoUrl)
            nameView.text = element().name
            cardView.setOnClickListener {
                cardView.isChecked = !cardView.isChecked
                if (cardView.isChecked) {
                    viewModel.newGroupUserList.add(element())
                } else viewModel.newGroupUserList.remove(element())
                Log.d(TAG, "bind: ${viewModel.newGroupUserList}")
            }

        }
    }


}
