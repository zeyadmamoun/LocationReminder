package com.example.android.locationreminder.savereminder

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.locationreminder.R
import com.example.android.locationreminder.data.FakeAndroidDataSource
import com.example.android.locationreminder.getOrAwaitValue
import com.example.android.locationreminder.locationreminders.data.ReminderDataSource
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderFragment
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import java.util.*


@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class SaveReminderFragmentTest {

    private lateinit var dataSource: ReminderDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        dataSource = FakeAndroidDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

//    @Before
//    fun registerIdlingResource() {
//        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
//    }
//
//    @After
//    fun unregisterIdlingResource() {
//        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
//    }

    @Test
    fun testingToast_whenNewReminderSaved() {
        val reminder = ReminderDataItem("Testing Reminder","Testing Desc","location",20.0,40.0,UUID.randomUUID().toString())
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(),R.style.Theme_LocationReminder)

        onView(withId(R.id.reminderTitle)).perform(replaceText(reminder.title))
        onView(withId(R.id.reminderDescription)).perform(replaceText(reminder.description))
        saveReminderViewModel.validateAndSaveReminder(reminder)

        assertEquals("Reminder Saved !", saveReminderViewModel.showToast.getOrAwaitValue())
    }

    // the same test but without using saveReminderViewModel and using ViewAssertions.
    // there is error happening on API lvl 30 with finding or matching toast --> https://github.com/android/android-test/issues/803
    @Test
    fun testingToastWithButton_whenNewReminderSaved() {
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(),R.style.Theme_LocationReminder)
        scenario.onFragment{
            it._viewModel.latitude.value = 20.0
            it._viewModel.longitude.value = 40.0
            it._viewModel.reminderSelectedLocationStr.value = "new location"
            it._viewModel.reminderTitle.value = "Testing Reminder"
            it._viewModel.reminderDescription.value = "Testing Desc"
        }

        val navController = mock(NavController::class.java)
        scenario.onFragment{
            Navigation.setViewNavController(it.view!!,navController)
        }

        onView(withId(R.id.reminderTitle)).perform(replaceText("Testing Reminder"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("Testing Desc"))
        onView(withId(R.id.saveReminder)).perform(click())

        scenario.onFragment {
            onView(ViewMatchers.withText("${it._viewModel.reminderTitle.value} geofence is created"))
                .inRoot(RootMatchers.withDecorView(CoreMatchers.not(it.requireActivity().window.decorView)))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        }
    }
}