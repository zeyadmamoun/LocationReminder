package com.example.android.locationreminder.reminderlist

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.locationreminder.R
import com.example.android.locationreminder.data.FakeAndroidDataSource
import com.example.android.locationreminder.locationreminders.data.ReminderDataSource
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderListFragment
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderListFragmentDirections
import com.example.android.locationreminder.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class ReminderListFragmentTest {

    private lateinit var dataSource: ReminderDataSource
    private lateinit var reminderListViewModel: RemindersListViewModel

    @Before
    fun setup() {
        dataSource = FakeAndroidDataSource()
        reminderListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), dataSource)
        stopKoin()

        val myModule = module {
            single {
                reminderListViewModel
            }
        }
        // new koin module
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun clickTask_NavigateToSaveReminderFragment(){
        val scenario = launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.Theme_LocationReminder)
        val navController = mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!,navController)
        }

        onView(withId(R.id.addReminderFAB))
            .perform(click())

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }

    //testing whether snackBar will show or not.
    @Test
    fun testingSnackbar_whenGetRemindersFail(){

        ReminderDataItem("title1","Description","Location1",20.0,40.0)

        dataSource.setErrorState(true)
        launchFragmentInContainer<ReminderListFragment>(Bundle(),R.style.Theme_LocationReminder)

        onView(withId(R.id.noDataTextView))
            .check(matches(isDisplayed()))
    }
}