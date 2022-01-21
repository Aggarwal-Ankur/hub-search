package com.aggarwalankur.hubsearch.network

import retrofit2.http.GET
import retrofit2.http.Query

interface GithubSearchService {
    @GET("search/users")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int
    ): UserList
}