package com.aggarwalankur.hubsearch.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aggarwalankur.hubsearch.data.local.RemoteKeys
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.network.GithubSearchService
import com.aggarwalankur.hubsearch.network.IN_QUALIFIER
import com.aggarwalankur.hubsearch.network.User
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

//Github page values start at 1, not 0
private const val GITHUB_STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class GithubRemoteMediator(
    private val query: String,
    private val service: GithubSearchService,
    private val userDatabase: UsersDatabase
) : RemoteMediator<Int, User>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(loadType: LoadType, state: PagingState<Int, User>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                Timber.d("Refresh remotekeys = " + remoteKeys)
                remoteKeys?.nextKey?.minus(1) ?: GITHUB_STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                Timber.d("Prepend remotekeys = " + remoteKeys + "; prevKey = " + prevKey)
                if (prevKey == null) {
                    Timber.d("Prepend2 remotekeys = " + remoteKeys + "; prevKey = " + prevKey)

                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }

                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                Timber.d("Append remotekeys = " + remoteKeys + "; nextKey = " + nextKey)
                if (nextKey == null) {
                    Timber.d("Append2 remotekeys = " + remoteKeys + "; nextKey = " + nextKey)
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
        }

        val apiQuery = query + IN_QUALIFIER

        try {
            Timber.d ("RemoteMediator : apiQuery = $apiQuery ; page=$page ; pagesize = ${state.config.pageSize}")
            val apiResponse = service.searchGithubUsers(apiQuery, page, state.config.pageSize)

            val users = apiResponse.items
            val endOfPaginationReached = users.isEmpty()
            userDatabase.withTransaction {
                // clear all data for users
                if (loadType == LoadType.REFRESH) {
                    userDatabase.remoteKeysDao().clearRemoteKeys()
                    userDatabase.usersDao().clear()
                }
                val prevKey = if (page == GITHUB_STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = users.map {
                    Timber.d("RemoteKeys inserted : userId = ${it.id} ; prev = $prevKey ; next = $nextKey")
                    RemoteKeys(userId = it.id, prevKey = prevKey, nextKey = nextKey)
                }

                userDatabase.remoteKeysDao().insertAll(keys)
                userDatabase.usersDao().insertAll(users)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, User>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { user ->
                // Get the remote keys of the last item retrieved
                val rk = userDatabase.remoteKeysDao().remoteKeysUserId(user.id)
                Timber.d("Remotekeys id = ${user.id} ; remoteKey=$rk")

                rk
            }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, User>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { user ->
                // Get the remote keys of the first items retrieved
                userDatabase.remoteKeysDao().remoteKeysUserId(user.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, User>
    ): RemoteKeys? {
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { userId ->
                userDatabase.remoteKeysDao().remoteKeysUserId(userId)
            }
        }
    }
}