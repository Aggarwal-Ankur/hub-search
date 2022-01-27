package com.aggarwalankur.hubsearch.data.local

import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.filters.SmallTest
import com.aggarwalankur.hubsearch.data.utils.toStarredUser
import com.aggarwalankur.hubsearch.network.FakeUserFactory
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
class UsersDatabaseTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: UsersDatabase
    private lateinit var userDao : UserDao
    private lateinit var starredUserDao : StarredUserDao

    private lateinit var userFactory: FakeUserFactory

    @Before
    fun setup() {
        hiltRule.inject()

        userDao = database.usersDao()
        starredUserDao = database.starredUserDao()
        userFactory = FakeUserFactory()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertUsersAndVerifyGetAll() {
        val user1 = userFactory.createUser("ABC", false)
        val user2 = userFactory.createUser("ABCD", false)
        val user3 = userFactory.createUser("2ABCD", false)

        val user4 = user1.copy()

        val userList = listOf(user1, user2, user3, user4)

        val expectedUserList = listOf(user4, user2, user3 )

        return runTest {
            userDao.insertAll(userList)
            val queryResult = userDao.getAllUsers()

            assertEquals(3, queryResult.size)
            assertEquals(expectedUserList, queryResult)
        }
    }

    @Test
    fun insertUnstarredUsers_CheckUsersByName() {
        val user1 = userFactory.createUser("ABC", false)
        val user2 = userFactory.createUser("ABCD", false)
        val user3 = userFactory.createUser("2ABCD", false)

        val user4 = user1.copy()

        val userList = listOf(user1, user2, user3, user4)

        val expectedUserList = listOf(user4, user2, user3 )

        return runTest {
            userDao.insertAll(userList)
            val queryResult = userDao.usersByName("%ABC%")
            val pagedDataFetched = queryResult.load(PagingSource.LoadParams.Refresh(
                key = null, loadSize = 10, placeholdersEnabled = false
            ))

            val listOfFetched = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data

            assertEquals(expectedUserList, listOfFetched)
        }
    }

    @Test
    fun insertUnstarredAndThenStarredUsers_CheckUserIsStarred() {
        val user1 = userFactory.createUser("ABC", false)
        val user2 = userFactory.createUser("ABCD", false)
        val user3 = userFactory.createUser("2ABCD", false)

        val user4 = user1.toStarredUser()

        val userList = listOf(user1, user2, user3)

        val expectedUserList = listOf(user1.copy(isStarred = true), user2, user3 )

        return runTest {
            userDao.insertAll(userList)
            starredUserDao.insert(user4)
            userDao.updateStarredUsers()

            val queryResult = userDao.usersByName("%ABC%")
            val pagedDataFetched = queryResult.load(PagingSource.LoadParams.Refresh(
                key = null, loadSize = 10, placeholdersEnabled = false
            ))

            val listOfFetched = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data

            assertEquals(expectedUserList, listOfFetched)
        }
    }

    @Test
    fun insertUnstarredUsersAndUpdateStarredstatus_CheckUserIsStarred() {
        val user1 = userFactory.createUser("ABC", false)
        val user2 = userFactory.createUser("ABCD", false)
        val user3 = userFactory.createUser("2ABCD", false)

        val userList = listOf(user1, user2, user3)

        val expectedUserList = listOf(user1.copy(isStarred = true), user2, user3 )

        return runTest {
            userDao.insertAll(userList)
            userDao.updateUserStarredStatus(user1.id, true)

            val queryResult = userDao.usersByName("%ABC%")
            val pagedDataFetched = queryResult.load(PagingSource.LoadParams.Refresh(
                key = null, loadSize = 10, placeholdersEnabled = false
            ))

            val listOfFetchedUsers = (pagedDataFetched as? PagingSource.LoadResult.Page)?.data

            assertEquals(expectedUserList, listOfFetchedUsers)

            userDao.updateUserStarredStatus(user1.id, false)
            val fetchedUser = userDao.getUserById(user1.id)

            assertEquals(user1, fetchedUser)
        }
    }
}