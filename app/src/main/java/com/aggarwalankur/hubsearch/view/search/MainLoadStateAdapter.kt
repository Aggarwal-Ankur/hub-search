package com.aggarwalankur.hubsearch.view.search

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

class MainLoadStateAdapter (private val retry: () -> Unit
) : LoadStateAdapter<MainLoadStateViewHolder>() {
    override fun onBindViewHolder(holder: MainLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): MainLoadStateViewHolder {
        return MainLoadStateViewHolder.create(parent, retry)
    }
}