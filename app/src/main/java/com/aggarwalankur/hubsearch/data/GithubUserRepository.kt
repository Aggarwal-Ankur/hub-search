package com.aggarwalankur.hubsearch.data

import androidx.lifecycle.SavedStateHandle
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aggarwalankur.hubsearch.data.local.StarredUserDao
import com.aggarwalankur.hubsearch.data.remote.GithubSearchPagingSource
import com.aggarwalankur.hubsearch.data.remote.GithubSearchPagingSource.Companion.NETWORK_PAGE_SIZE
import com.aggarwalankur.hubsearch.network.GithubSearchService
import com.aggarwalankur.hubsearch.network.User
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class GithubUserRepository @Inject constructor(private val service: GithubSearchService,
                                               private val starredUserDao : StarredUserDao
){
    fun getSearchResultStream(query: String): Flow<PagingData<User>> {
        Timber.d("New query: $query")
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { GithubSearchPagingSource(service, starredUserDao, query) }
        ).flow
    }

}