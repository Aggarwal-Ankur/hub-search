package com.aggarwalankur.hubsearch.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aggarwalankur.hubsearch.network.GithubSearchService
import com.aggarwalankur.hubsearch.network.User
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class GithubSearchPagingSource (
    private val service: GithubSearchService,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val position = params.key ?: GITHUB_STARTING_PAGE_INDEX
        val apiQuery = query + IN_QUALIFIER
        return try {
            val response = service.searchRepos(apiQuery, position, params.loadSize)
            val repos = response.items
            val nextKey = if (repos.isEmpty()) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / NETWORK_PAGE_SIZE)
            }
            LoadResult.Page(
                data = repos,
                prevKey = if (position == GITHUB_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: IOException) {
            Timber.d("Exception = ${exception.stackTrace}")
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            Timber.d("Exception = ${exception.stackTrace}")
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
    companion object {
        const val GITHUB_STARTING_PAGE_INDEX = 1
        const val NETWORK_PAGE_SIZE = 30
        const val IN_QUALIFIER = "in:login"
    }
}