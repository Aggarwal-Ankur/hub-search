package com.aggarwalankur.hubsearch.view.starred

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.databinding.FragmentMainBinding
import com.aggarwalankur.hubsearch.databinding.FragmentStarredBinding
import com.aggarwalankur.hubsearch.network.User
import com.aggarwalankur.hubsearch.view.MainViewModel
import com.aggarwalankur.hubsearch.view.search.ItemViewHolder
import com.aggarwalankur.hubsearch.view.search.MainFragmentDirections
import com.aggarwalankur.hubsearch.view.search.UserBindingAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber


class StarredFragment : Fragment(),  ItemViewHolder.OnClickListener {
    private var _binding: FragmentStarredBinding? = null
    private val binding get() = _binding!!

    val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStarredBinding.inflate(inflater, container, false)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.starredList.addItemDecoration(decoration)


        // bind the state
        binding.bindState(viewModel.pagingStarredUsersFlow)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onUserClick(user: User) {
        val action = StarredFragmentDirections.navigateToDetailsFragment2(user)
        findNavController().navigate(action)
    }

    override fun onUserStarClick(user: User) {
        viewModel.toggleStar(user)
    }

    private fun FragmentStarredBinding.bindState(pagingData: Flow<PagingData<User>>){
        val userAdapter = UserBindingAdapter(this@StarredFragment)

        starredList.adapter = userAdapter

        lifecycleScope.launch {
            pagingData.collectLatest(userAdapter::submitData)
        }

        lifecycleScope.launch {
            userAdapter.loadStateFlow.collectLatest {
                val isEmpty = userAdapter.itemCount == 0
                emptyList.isVisible = isEmpty
                starredList.isVisible = !isEmpty
            }
        }
    }

}