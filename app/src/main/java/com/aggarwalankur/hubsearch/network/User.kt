package com.aggarwalankur.hubsearch.network

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "users"/*, indices = [Index(value = ["login"], unique = true)]*/)
@Parcelize
data class User (

    @PrimaryKey(autoGenerate = true)
    val user_key: Long,


    @field:SerializedName("id") val id : Long,
    @field:SerializedName("login") val login : String,
    @field:SerializedName("type") val type : String,

    @field:SerializedName("html_url")
    val profileUrl : String,

    @field:SerializedName("avatar_url")
    val avatarUrl : String,

    @field:SerializedName("followers_url")
    val followersUrl : String,

    @field:SerializedName("repos_url")
    val reposUrl : String,

    var isStarred : Boolean = false

) : Parcelable