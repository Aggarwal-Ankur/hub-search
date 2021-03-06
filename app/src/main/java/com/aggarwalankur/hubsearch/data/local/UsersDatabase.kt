package com.aggarwalankur.hubsearch.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aggarwalankur.hubsearch.network.User

@Database( entities = [User::class, RemoteKeys::class, StarredUser::class],
    version = 1, exportSchema = false )
abstract class UsersDatabase : RoomDatabase() {
    abstract fun usersDao() : UserDao
    abstract fun remoteKeysDao() : RemoteKeysDao
    abstract fun starredUserDao() : StarredUserDao

    companion object {
        val DB_NAME = "Github.db"
    }
}