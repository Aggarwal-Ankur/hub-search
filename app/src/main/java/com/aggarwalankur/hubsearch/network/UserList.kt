package com.aggarwalankur.hubsearch.network

import com.squareup.moshi.Json

data class UserList (
    @Json(name = "items")
    val items: List<User> = emptyList(),

    @Json(name = "total_count")
    val totalCount: Int,

    val nextPage: Int? = null
)
