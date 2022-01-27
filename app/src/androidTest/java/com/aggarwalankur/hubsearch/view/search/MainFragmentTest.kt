package com.aggarwalankur.hubsearch.view.search
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.aggarwalankur.hubsearch.R
import com.aggarwalankur.hubsearch.data.GithubUserRepository
import com.aggarwalankur.hubsearch.data.local.UsersDatabase
import com.aggarwalankur.hubsearch.launchFragmentInHiltContainer
import com.aggarwalankur.hubsearch.network.FakeUserFactory
import com.aggarwalankur.hubsearch.network.User
import com.aggarwalankur.hubsearch.util.saveUserBlocking
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@MediumTest
@ExperimentalCoroutinesApi
class MainFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var repository: GithubUserRepository

    @Inject
    lateinit var database: UsersDatabase

    private lateinit var userFactory: FakeUserFactory
    private lateinit var user1: User
    private lateinit var user2: User
    private lateinit var user3: User
    private lateinit var user4: User

    @Before
    fun init() {
        // Populate @Inject fields in test class
        hiltRule.inject()
        userFactory = FakeUserFactory()

        user1 = userFactory.createUser("ABC", false)
        user2 = userFactory.createUser("ABCD", false)
        user3 = userFactory.createUser("DABCD", false)
        user4 = userFactory.createUser("XYZ", false)
    }

    @Test
    fun initialElementsAreAsExpected() {

        return runTest {
            launchFragmentInHiltContainer<MainFragment>()

            onView(withId(R.id.progress_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.input_layout)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun initialListWhenUsersInserted() {
        database.saveUserBlocking(user1)
        database.saveUserBlocking(user2)
        database.saveUserBlocking(user3)
        database.saveUserBlocking(user4)

        return runTest {
            launchFragmentInHiltContainer<MainFragment>()

            onView(withText("ABC")).check(matches(isDisplayed()))
            onView(withText("ABCD")).check(matches(isDisplayed()))
            onView(withText("DABCD")).check(matches(isDisplayed()))
            onView(withText("XYZ")).check(matches(isDisplayed()))
        }
    }

    @Test
    fun filterWithInputText_NoListReturned() {
        database.saveUserBlocking(user1)
        database.saveUserBlocking(user2)
        database.saveUserBlocking(user3)
        database.saveUserBlocking(user4)
        return runTest {
            launchFragmentInHiltContainer<MainFragment>()

            onView(withId(R.id.search_user)).perform(typeText("barara"))

            //Not too happy about this but have to wait for flow to emit
            sleep(3_000L)

            //Error must be displayed on the screen
            onView(withId(R.id.list)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.emptyList)).check(matches(isDisplayed()))


        }
    }
}