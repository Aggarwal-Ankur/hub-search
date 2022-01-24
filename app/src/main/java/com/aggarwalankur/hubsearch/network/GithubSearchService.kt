package com.aggarwalankur.hubsearch.network

import retrofit2.http.GET
import retrofit2.http.Query

const val IN_QUALIFIER = ""

interface GithubSearchService {
    @GET("search/users?sort=login")
    suspend fun searchGithubUsers(
        @Query("q", encoded = true) query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): UserList
}