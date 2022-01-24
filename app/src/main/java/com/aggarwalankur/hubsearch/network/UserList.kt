package com.aggarwalankur.hubsearch.network

import com.google.gson.annotations.SerializedName

data class UserList (
    @field:SerializedName( "items")
    val items: List<User> = emptyList(),

    @field:SerializedName("total_count")
    val totalCount: Int,

    val nextPage: Int? = null
)
