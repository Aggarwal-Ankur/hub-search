package com.aggarwalankur.hubsearch.view.search

import androidx.recyclerview.widget.RecyclerView
import com.aggarwalankur.hubsearch.databinding.ItemUserBinding
import com.aggarwalankur.hubsearch.network.User
import javax.inject.Inject

class ItemViewHolder @Inject constructor(
    private val binding: ItemUserBinding,
    private val onClickListener: OnClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        binding.user = user

        binding.root.setOnClickListener {
            onClickListener.onUserClick(user)
        }
    }

    interface OnClickListener {
        fun onUserClick(user: User)
    }
}