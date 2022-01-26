package com.aggarwalankur.hubsearch.data.local

import androidx.paging.PagingSource
import androidx.room.*
import com.aggarwalankur.hubsearch.network.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert (user : User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<User>)

    @Query("UPDATE users SET isStarred=1 WHERE EXISTS (SELECT * FROM starred_users WHERE login LIKE users.login)")
    suspend fun updateStarredUsers ()

    //@Query ("DELETE FROM users WHERE id = :id")
    //suspend fun deleteUser (id : Int)

    @Query("DELETE FROM users")
    suspend fun clear()

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>


    @Query("SELECT * FROM users WHERE login LIKE :queryString")
    fun usersByName(queryString : String): PagingSource<Int, User>

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id : Long) : User?

    @Query("UPDATE users SET isStarred=:isStarred WHERE id = :userId")
    suspend fun updateUserStarredStatus(userId : Long, isStarred : Boolean)

}