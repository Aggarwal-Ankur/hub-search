package com.aggarwalankur.hubsearch.view

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.aggarwalankur.hubsearch.data.GithubUserRepository
import com.aggarwalankur.hubsearch.network.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Viewmodel for the main screen as well as the details screen.
 *
 * Marking it as OptIn because flatMapLatest is experimental
 */
@HiltViewModel
class MainViewModel @Inject constructor ( private val repository: GithubUserRepository,
                                          private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state: StateFlow<UiState>

    val pagingDataFlow: Flow<PagingData<User>>

    //Processes any errors from UI
    val accept: (UiAction) -> Unit

    //Showing on details screen
    private val _selectedUser = MutableLiveData<User>()
    val selectedUser : LiveData<User>
        get() = _selectedUser

    private val _selectedUserIsStarred = MutableLiveData<Boolean>()
    val selectedUserIsStarred: LiveData<Boolean>
        get() = _selectedUserIsStarred

    init {
        val initialQuery: String = savedStateHandle.get(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        val lastQueryScrolled: String = savedStateHandle.get(LAST_QUERY_SCROLLED) ?: DEFAULT_QUERY
        val actionStateFlow = MutableSharedFlow<UiAction>()
        val searches = actionStateFlow
            .filterIsInstance<UiAction.Search>()
            .distinctUntilChanged()
            .onStart { emit(UiAction.Search(query = initialQuery)) }
        val queriesScrolled = actionStateFlow
            .filterIsInstance<UiAction.Scroll>()
            .distinctUntilChanged()
            // This is shared to keep the flow "hot" while caching the last query scrolled,
            // otherwise each flatMapLatest invocation would lose the last query scrolled,
            .shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                replay = 1
            )
            .onStart { emit(UiAction.Scroll(currentQuery = lastQueryScrolled)) }

        pagingDataFlow = searches
            .flatMapLatest { searchUsers(queryString = it.query) }
            .cachedIn(viewModelScope)

        state = combine(
            searches,
            queriesScrolled,
            ::Pair
        ).map { (search, scroll) ->
            UiState(
                query = search.query,
                lastQueryScrolled = scroll.currentQuery,
                // If the search query matches the scroll query, the user has scrolled
                hasNotScrolledForCurrentSearch = search.query != scroll.currentQuery
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
                initialValue = UiState()
            )

        accept = { action ->
            viewModelScope.launch { actionStateFlow.emit(action) }
        }

    }

    override fun onCleared() {
        savedStateHandle[LAST_SEARCH_QUERY] = state.value.query
        savedStateHandle[LAST_QUERY_SCROLLED] = state.value.lastQueryScrolled
        super.onCleared()
    }

    fun setSelectedUser (user: User?) {
        user?.let {
            _selectedUser.value = it
            _selectedUserIsStarred.value = it.isStarred
        }
    }

    fun toggleStarForSelectedUser(user : User) {
        //First, create the changed user
        val changedUser = user.copy(isStarred = !user.isStarred)
        _selectedUser.value = changedUser

        //We have just set the user ^
        _selectedUserIsStarred.value = selectedUser.value!!.isStarred

        _selectedUserIsStarred.value?.let {
            if(it) {
                starUser(changedUser)
            }else {
                unstarUser(changedUser)
            }
        }
    }

    fun toggleStar (user : User) {
        //First, create the changed user
        val changedUser = user.copy(isStarred = !user.isStarred)
        Timber.d("toggleStar : user=${user.isStarred} ; changed = ${changedUser.isStarred}")
        if(changedUser.isStarred) {
            starUser(changedUser)
        }else {
            unstarUser(changedUser)
        }
    }


    private fun starUser(user : User) {
        viewModelScope.launch {
            repository.insertStarredUser(user)
            Timber.d ("User ${user.login} starred")
        }
    }

    private fun unstarUser (user: User) {
        viewModelScope.launch {
            repository.deleteStarredUser(user)
            Timber.d ("User ${user.login} unstarred")
        }
    }

    private fun searchUsers(queryString: String): Flow<PagingData<User>> =
        repository.getSearchResultStream(queryString)
            //We may insert paging separators, but for now, not doing this


}

sealed class UiAction {
    data class Search(val query: String) : UiAction()
    data class Scroll(val currentQuery: String) : UiAction()
}

data class UiState(
    val query: String = DEFAULT_QUERY,
    val lastQueryScrolled: String = DEFAULT_QUERY,
    val hasNotScrolledForCurrentSearch: Boolean = false
)

private const val LAST_QUERY_SCROLLED: String = "last_query_scrolled"
private const val LAST_SEARCH_QUERY: String = "last_search_query"
private const val DEFAULT_QUERY = "agg"