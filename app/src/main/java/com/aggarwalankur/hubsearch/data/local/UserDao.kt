package com.aggarwalankur.hubsearch.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.aggarwalankur.hubsearch.network.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (user : User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(repos: List<User>)

    //@Query ("DELETE FROM users WHERE id = :id")
    //suspend fun deleteUser (id : Int)

    @Query("DELETE FROM users")
    suspend fun clear()

    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>


    @Query("SELECT * FROM users WHERE login LIKE :queryString")
    fun usersByName(queryString : String): PagingSource<Int, User>

}