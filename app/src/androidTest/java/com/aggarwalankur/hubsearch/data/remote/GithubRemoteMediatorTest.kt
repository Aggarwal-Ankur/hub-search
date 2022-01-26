package com.aggarwalankur.hubsearch.data.remote

import androidx.paging.*
import androidx.test.filters.SmallTest
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.network.FakeGithubSearchService
import com.aggarwalankur.hubsearch.network.FakeUserFactory
import com.aggarwalankur.hubsearch.network.User
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
class GithubRemoteMediatorTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: UsersDatabase

    private lateinit var fakeGithubSearchService : FakeGithubSearchService
    private lateinit var userFactory: FakeUserFactory

    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    @Before
    fun setup() {
        hiltRule.inject()
        fakeGithubSearchService = FakeGithubSearchService()
        userFactory = FakeUserFactory()

        user1 = userFactory.createUser("ABC", false)
        user2 = userFactory.createUser("ABCD", false)
        user3 = userFactory.createUser("2ABCD", false)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @ExperimentalPagingApi
    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        fakeGithubSearchService.addFakeUser(user1)
        fakeGithubSearchService.addFakeUser(user2)
        fakeGithubSearchService.addFakeUser(user3)

        val githubRemoteMediator = GithubRemoteMediator(
            "AB",
            fakeGithubSearchService,
            database
        )

        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = githubRemoteMediator.load(LoadType.REFRESH, pagingState)

        assertThat(result, instanceOf(RemoteMediator.MediatorResult.Success::class.java))
        assertFalse ((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @ExperimentalPagingApi
    @Test
    fun refreshLoadReturnsFailureWhenNoMoreDataIsPresent() = runTest {
        //To test this, do not enter data in fakeGithubService

        val githubRemoteMediator = GithubRemoteMediator(
            "AB",
            fakeGithubSearchService,
            database
        )

        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = githubRemoteMediator.load(LoadType.REFRESH, pagingState)

        assertThat(result, instanceOf(RemoteMediator.MediatorResult.Success::class.java))
        assertTrue ((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @ExperimentalPagingApi
    @Test
    fun mediatorReturnsErrorWhenQueryIsBlank() = runTest {
        //Enter data by search query is blank

        fakeGithubSearchService.addFakeUser(user1)
        fakeGithubSearchService.addFakeUser(user2)
        fakeGithubSearchService.addFakeUser(user3)

        val githubRemoteMediator = GithubRemoteMediator(
            "",
            fakeGithubSearchService,
            database
        )

        val pagingState = PagingState<Int, User>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )

        val result = githubRemoteMediator.load(LoadType.REFRESH, pagingState)

        assertThat(result, instanceOf(RemoteMediator.MediatorResult.Error::class.java))
    }


}