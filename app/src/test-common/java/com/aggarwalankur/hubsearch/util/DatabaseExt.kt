package com.aggarwalankur.hubsearch.util

import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.data.utils.toStarredUser
import com.aggarwalankur.hubsearch.network.User
import kotlinx.coroutines.runBlocking

fun UsersDatabase.saveUserBlocking(user: User) = runBlocking {
    this@saveUserBlocking.usersDao().insert(user)
}

fun UsersDatabase.saveStarredUserBlocking(user: User) = runBlocking {
    this@saveStarredUserBlocking.starredUserDao().insert(user.toStarredUser())
}