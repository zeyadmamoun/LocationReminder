package com.example.android.locationreminder.savereminder

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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

    // the same test but there is error happening when i click on the saveReminder button as you mentioned and i told you there is error in espresso i will mention it.
    @Test
    fun testingToastWithButton_whenNewReminderSaved() {
        val reminder = ReminderDataItem("Testing Reminder","Testing Desc","location",20.0,40.0,UUID.randomUUID().toString())
        val scenario = launchFragmentInContainer<SaveReminderFragment>(Bundle(),R.style.Theme_LocationReminder)
        saveReminderViewModel.longitude.value = 20.0
        saveReminderViewModel.latitude.value = 40.0
        saveReminderViewModel.selectedPOI.value = PointOfInterest(LatLng(40.0,20.0),"2","Location")
        saveReminderViewModel.reminderSelectedLocationStr.value = "Location"

        onView(withId(R.id.reminderTitle)).perform(replaceText("Testing Reminder"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("Testing Desc"))
        onView(withId(R.id.saveReminder)).perform(click())

        assertEquals("Reminder Saved !", saveReminderViewModel.showToast.getOrAwaitValue())
    }
}