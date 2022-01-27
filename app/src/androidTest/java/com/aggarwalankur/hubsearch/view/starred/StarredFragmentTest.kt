package com.aggarwalankur.hubsearch.view.starred

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.launchFragmentInHiltContainer
import com.aggarwalankur.hubsearch.network.FakeUserFactory
import com.aggarwalankur.hubsearch.network.User
import com.aggarwalankur.hubsearch.util.saveStarredUserBlocking
import com.aggarwalankur.hubsearch.util.saveUserBlocking
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.lang.Thread.sleep
import javax.inject.Inject

@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class StarredFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: UsersDatabase

    private lateinit var userFactory: FakeUserFactory
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()

        userFactory = FakeUserFactory()
        user1 = userFactory.createUser("ABC", true)
        user2 = userFactory.createUser("ABCD", false)
        user3 = userFactory.createUser("XYZ", true)

        database.saveUserBlocking(user1)
        database.saveUserBlocking(user2)
        database.saveUserBlocking(user3)

        database.saveStarredUserBlocking(user1)
        database.saveStarredUserBlocking(user3)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun starredUsersAreDisplayed() {

        return runTest {
            launchFragmentInHiltContainer<StarredFragment>()

            sleep(1000)

            onView(withText("ABC")).check(matches(isDisplayed()))
            onView(withText("XYZ")).check(matches(isDisplayed()))

            onView(withText("ABCD")).check(doesNotExist())
        }
    }

}