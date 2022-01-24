package com.aggarwalankur.hubsearch.data.local

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import com.aggarwalankur.hubsearch.network.User

@Dao
interface StarredUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (user : User)

    //@Query ("DELETE FROM users WHERE user_key = :id")
    //suspend fun delete (id : Int)

    @Query("DELETE FROM users")
    suspend fun clear()

    @Query("SELECT * FROM users")
    fun getStarredUsers(): List<User>

    //@Query("SELECT count() FROM users WHERE user_key = :id")
    //fun isUserStarred(id : Int) : LiveData<Int>

    @Query("SELECT * FROM users ORDER BY login ASC")
    fun usersSortedByName(): PagingSource<Int, User>

}