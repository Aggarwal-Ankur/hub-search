package com.aggarwalankur.hubsearch.view.details


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.data.GithubUserRepository
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.launchFragmentInHiltContainer
import com.aggarwalankur.hubsearch.network.FakeUserFactory
import com.aggarwalankur.hubsearch.network.GithubSearchService
import com.aggarwalankur.hubsearch.view.MainViewModel
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class DetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var database: UsersDatabase

    private lateinit var userFactory: FakeUserFactory

    @Inject
    lateinit var searchService : GithubSearchService

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()

        userFactory = FakeUserFactory()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun userIsDisplayed() {
        val user1 = userFactory.createUser("ABC", false)
        val bundle = DetailsFragmentArgs(selectedUser = user1).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(bundle)

        onView(withId(R.id.nameTv)).check(matches(isDisplayed()))
        onView(withId(R.id.nameTv)).check(matches(withText("ABC")))

        onView(withId(R.id.user_type)).check(matches(isDisplayed()))
        onView(withId(R.id.starIv)).check(matches(isDisplayed()))
        //Truth.assertThat(user1).isNotEmpty()

    }

    @Test
    fun checkSelectedUserInViewmodel() {
        val user1 = userFactory.createUser("ABC", false)
        val bundle = DetailsFragmentArgs(selectedUser = user1).toBundle()

        launchFragmentInHiltContainer<DetailsFragment>(bundle)

        return runTest {
            database.usersDao().insert(user1)
            val fetchedUser = database.usersDao().getUserById(user1.id)

            //user not starred initially
            Truth.assertThat(fetchedUser!!.isStarred).isFalse()

            onView(withId(R.id.starIv)).perform(click())

            //there is a starred user after the click
            val fetchedUserAgain = database.usersDao().getUserById(user1.id)
            Truth.assertThat(fetchedUserAgain).isNotNull()

            Truth.assertThat(fetchedUserAgain!!.isStarred).isTrue()

        }


        //Truth.assertThat(user1).isNotEmpty()

    }

}