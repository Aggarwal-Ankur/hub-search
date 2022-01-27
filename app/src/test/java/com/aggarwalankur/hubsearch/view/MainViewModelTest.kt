package com.aggarwalankur.hubsearch.view

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.aggarwalankur.hubsearch.data.GithubUserRepository
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.network.FakeGithubSearchService
import com.aggarwalankur.hubsearch.network.FakeUserFactory
import com.aggarwalankur.hubsearch.network.GithubSearchService
import com.aggarwalankur.hubsearch.network.User
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MainViewModelTest {
    private lateinit var userRepository: GithubUserRepository
    private lateinit var usersDatabase: UsersDatabase
    private lateinit var githubSearchService: GithubSearchService

    private lateinit var viewmodel:MainViewModel

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var userFactory: FakeUserFactory
    private lateinit var user1: User

    @Before
    fun setup() {
        userFactory = FakeUserFactory()
        user1 = userFactory.createUser("ABC", false)

        githubSearchService = FakeGithubSearchService()
        usersDatabase = Room.inMemoryDatabaseBuilder(getApplicationContext(), UsersDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userRepository = GithubUserRepository(githubSearchService, usersDatabase)

        viewmodel = MainViewModel(userRepository, SavedStateHandle())
    }

    @After
    fun cleanUp() {
    }

    @Test
    fun setSelectedUserInViewmodel() {
        viewmodel.setSelectedUser(user1)

        Truth.assertThat(viewmodel.selectedUser.value).isNotNull()
        Truth.assertThat(viewmodel.selectedUser.value!!.isStarred).isFalse()
    }

    @Test
    fun starTheSelectedUserInViewmodel() {
        viewmodel.setSelectedUser(user1)

        Truth.assertThat(viewmodel.selectedUser.value!!.isStarred).isFalse()
        viewmodel.toggleStarForSelectedUser(user1)
        Truth.assertThat(viewmodel.selectedUser.value!!.isStarred).isTrue()

    }
}