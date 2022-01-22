package com.aggarwalankur.hubsearch.network

sealed class SearchResult {
    data class Success(val data: List<User>) : SearchResult()
    data class Error(val error: Exception) : SearchResult()
}