package com.aggarwalankur.hubsearch.network

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "users")
@Parcelize
data class User (

    @PrimaryKey(autoGenerate = false)
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

    var isStarred : Boolean = false

) : Parcelable