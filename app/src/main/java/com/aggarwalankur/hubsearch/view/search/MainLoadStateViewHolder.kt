package com.aggarwalankur.hubsearch.view.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.databinding.LoadStateFooterItemBinding

class MainLoadStateViewHolder(
    private val binding: LoadStateFooterItemBinding,
    retry: () -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.progressBar.isVisible = loadState is LoadState.Loading
        binding.errorMsg.isVisible = loadState is LoadState.Error
    }

    companion object {
        fun create(parent: ViewGroup, retry: () -> Unit): MainLoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.load_state_footer_item, parent, false)
            val binding = LoadStateFooterItemBinding.bind(view)
            return MainLoadStateViewHolder(binding, retry)
        }
    }
}