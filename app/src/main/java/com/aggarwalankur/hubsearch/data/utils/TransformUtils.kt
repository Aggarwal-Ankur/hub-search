package com.aggarwalankur.hubsearch.data.utils

import com.aggarwalankur.hubsearch.data.local.StarredUser
import com.aggarwalankur.hubsearch.network.User

fun User.toStarredUser() = StarredUser (
    id = id, login = login, type = type, profileUrl = profileUrl,
    avatarUrl = avatarUrl, followersUrl = followersUrl, reposUrl = reposUrl,
    isStarred = true,
    user_key = 0
)