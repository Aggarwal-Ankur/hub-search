package com.aggarwalankur.hubsearch.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aggarwalankur.hubsearch.data.local.StarredUser
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.data.remote.GithubRemoteMediator
import com.aggarwalankur.hubsearch.data.utils.toStarredUser
import com.aggarwalankur.hubsearch.network.GithubSearchService
import com.aggarwalankur.hubsearch.network.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GithubUserRepository @Inject constructor(private val service: GithubSearchService,
                                               private val database : UsersDatabase
){
    fun getSearchResultStream(query: String): Flow<PagingData<User>> {

        // appending '%' so we can allow other characters to be before and after the query string
        val dbQuery = "%${query.replace(' ', '%')}%"
        val pagingSourceFactory = { database.usersDao().usersByName(dbQuery) }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = GithubRemoteMediator(
                query,
                service,
                database
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    fun getStarredUsersStream() : Flow<PagingData<StarredUser>> {
        val pagingSourceFactory = { database.starredUserDao().getStarredUsers() }

        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(pageSize = NETWORK_PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    suspend fun insertStarredUser(user: User) {
        withContext(Dispatchers.IO) {
            user.isStarred = true
            database.starredUserDao().insert(user.toStarredUser())
            database.usersDao().updateUser(user.id, true)
        }
    }

    suspend fun deleteStarredUser(user: User) {
        user.isStarred = false
        withContext(Dispatchers.IO) {
            database.starredUserDao().delete(user.id)
            database.usersDao().updateUser(user.id, false)
        }
    }

    companion object {
        const val NETWORK_PAGE_SIZE = 100
    }

}