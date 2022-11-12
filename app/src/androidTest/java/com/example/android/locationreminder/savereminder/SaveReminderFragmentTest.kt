package com.example.android.locationreminder.savereminder

import android.os.Bundle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.locationreminder.*
import com.example.android.locationreminder.data.FakeAndroidDataSource
import com.example.android.locationreminder.locationreminders.RemindersActivity
import com.example.android.locationreminder.locationreminders.data.ReminderDataSource
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.example.android.locationreminder.locationreminders.reminderslist.RemindersListViewModel
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import org.junit.After
import org.junit.Assert.assertEquals


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

    @Test
    fun testingToast_whenNewReminderSaved() {

//        val reminder = ReminderDataItem("title1","Description","Location1",20.0,40.0)
//        saveReminderViewModel.saveReminder(reminder)

        saveReminderViewModel.latitude.postValue(20.0)
        saveReminderViewModel.longitude.postValue(40.0)
        saveReminderViewModel.selectedPOI.postValue(PointOfInterest(LatLng(20.0, 40.0), "3", "Location"))

        val activity = ActivityScenario.launch(RemindersActivity::class.java)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("Testing Reminder"))

        onView(withId(R.id.reminderDescription)).perform(replaceText("Testing Desc"))

        onView(withId(R.id.addReminderBtn)).perform(click())
        assertEquals("Reminder Saved !",saveReminderViewModel.showToast.getOrAwaitValue())

    }
}