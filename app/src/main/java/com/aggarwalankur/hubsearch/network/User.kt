package com.aggarwalankur.hubsearch.network

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User (
    val id: Int,
    val login : String,
    val type : String,

    @Json(name = "html_url")
    val profileUrl : String,

    @Json(name = "avatar_url")
    val avatarUrl : String,

    @Json(name = "followers_url")
    val followersUrl : String,

    @Json(name = "repos_url")
    val reposUrl : String,

) : Parcelable