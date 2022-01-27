package com.aggarwalankur.hubsearch.view.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.databinding.FragmentMainBinding
import com.aggarwalankur.hubsearch.network.User
import com.aggarwalankur.hubsearch.view.MainViewModel
import com.aggarwalankur.hubsearch.view.UiAction
import com.aggarwalankur.hubsearch.view.UiState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainFragment : Fragment(),  ItemViewHolder.OnClickListener{
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    val viewModel: MainViewModel by hiltNavGraphViewModels(R.id.nav_graph)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        // bind the state
        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.show_starred_users_menu -> {
                val action = MainFragmentDirections.navigateToStarredFragment()
                findNavController().navigate(action)
            }
        }

        return true
    }


    override fun onUserClick(user: User) {
        val action = MainFragmentDirections.navigateToDetailsFragment(user)
        findNavController().navigate(action)
    }

    override fun onUserStarClick(user: User) {
        viewModel.toggleStar(user)
    }

    private fun FragmentMainBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<User>>,
        uiActions: (UiAction) -> Unit
    ) {
        val userAdapter = UserBindingAdapter(this@MainFragment)
        list.adapter = userAdapter.withLoadStateFooter(
            footer = MainLoadStateAdapter { userAdapter.retry() }
        )
        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            userAdapter = userAdapter,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun FragmentMainBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {

        searchUser.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                searchUser.setSelection(searchUser.text.length)
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                updateUserListFromInput(onQueryChanged)
            }
        })


        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect(searchUser::setText)
        }
    }

    private fun FragmentMainBinding.updateUserListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchUser.text.trim().let {

            list.scrollToPosition(0)
            onQueryChanged(UiAction.Search(query = it.toString()))

        }
    }

    private fun FragmentMainBinding.bindList(
        userAdapter: UserBindingAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<User>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {
        retryButton.setOnClickListener { userAdapter.retry() }
        swipeLayout.setOnRefreshListener {
            swipeLayout.isRefreshing = false
            userAdapter.retry()
        }
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })
        val notLoading = userAdapter.loadStateFlow
            // Only emit when REFRESH LoadState for RemoteMediator changes.
            .distinctUntilChangedBy { it.source.refresh }
            // Only react to cases where Remote REFRESH completes i.e., NotLoading.
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest(userAdapter::submitData)
        }

        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }

        lifecycleScope.launch {
            userAdapter.loadStateFlow.collect { loadState ->

                val isListEmpty = loadState.refresh is LoadState.NotLoading && userAdapter.itemCount == 0
                // show empty list
                emptyList.isVisible = isListEmpty
                // Only show the list if refresh succeeds.
                list.isVisible = !isListEmpty
                // Show loading spinner during initial load or refresh.
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                // Show the retry state if initial load or refresh fails.
                retryButton.isVisible = loadState.mediator?.refresh is LoadState.Error
                        && userAdapter.itemCount == 0 && searchUser.text.trim().length > 0
                // Toast on any error, regardless of whether it came from RemoteMediator or PagingSource
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    val snackbar = Snackbar.make(binding.mainLayout, getString(R.string.search_error_short),
                        Snackbar.LENGTH_INDEFINITE)
                    snackbar.setAction(getString(R.string.ok), View.OnClickListener {
                        snackbar.dismiss()
                    })

                    snackbar.show()
                }
            }
        }
    }
}