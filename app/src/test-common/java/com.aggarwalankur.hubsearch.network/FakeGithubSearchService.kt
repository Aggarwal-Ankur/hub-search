package com.aggarwalankur.hubsearch.network

import retrofit2.http.Query
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

class FakeGithubSearchService  @Inject constructor() : GithubSearchService {

    var failureMsg: String? = null
    private val items : MutableList<User> = arrayListOf()

    fun addFakeUser(user : User){
        items.add(user)
    }

    override suspend fun searchGithubUsers(
        @Query("q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): UserList {
        failureMsg?.let {
            throw IOException(it)
        }

        val returnItems = getUsers(page, itemsPerPage)

        return UserList(
            items = returnItems,
            totalCount = returnItems.count(),
            nextPage = if (returnItems.count() < itemsPerPage) null else page+1
        )
    }

    private fun getUsers(page:Int, itemsPerPage: Int) : List<User> {
        //Github repo is 1-based
        if (page<1) {
            return items.subList(0, min(items.size, itemsPerPage))
        }
        val startPos = (page-1) * itemsPerPage
        return items.subList(startPos, min(items.size, startPos + itemsPerPage))
    }
}