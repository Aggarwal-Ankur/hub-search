package com.aggarwalankur.hubsearch.data.local

import androidx.room.*
import com.aggarwalankur.hubsearch.network.User

@Dao
interface StarredUserDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert (starredUser : StarredUser)

    @Query("DELETE FROM starred_users WHERE id = :id")
    suspend fun delete(id : Long)

    @Query("SELECT * FROM starred_users")
    suspend fun getStarredUsers(): List<User>

}