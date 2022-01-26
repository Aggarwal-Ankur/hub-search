package com.aggarwalankur.hubsearch.network

import java.util.concurrent.atomic.AtomicLong

class FakeUserFactory {
    private val counter = AtomicLong(0)

    fun createUser(userName: String, isStarred: Boolean): User {
        val id = counter.incrementAndGet()
        return User(
            user_key = id,
            id = 1000L + id,
            login = userName,
            type = "User",
            profileUrl = "https://github.com/$userName",
            avatarUrl = "https://avatars.githubusercontent.com/u/$userName?v=4",
            followersUrl = "https://api.github.com/users/$userName/followers",
            reposUrl = "https://api.github.com/users/$userName/repos",
            isStarred = isStarred
        )
    }
}