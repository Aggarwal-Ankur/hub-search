package com.aggarwalankur.hubsearch.view.starred

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.databinding.FragmentStarredBinding
import com.aggarwalankur.hubsearch.network.User
import com.aggarwalankur.hubsearch.view.MainViewModel
import com.aggarwalankur.hubsearch.view.search.ItemViewHolder
import com.aggarwalankur.hubsearch.view.search.UserBindingAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class StarredFragment : Fragment(),  ItemViewHolder.OnClickListener {
    private lateinit var _binding: FragmentStarredBinding

    private val viewModel :MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_starred, container, false)
        _binding = FragmentStarredBinding.bind(root)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        _binding.starredList.addItemDecoration(decoration)


        // bind the state
        _binding.bindState(viewModel.pagingStarredUsersFlow)

        return _binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
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