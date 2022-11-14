package com.example.android.locationreminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.locationreminder.data.FakeAndroidDataSource
import com.example.android.locationreminder.locationreminders.RemindersActivity
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderViewModel
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest {

    private lateinit var dataSource: FakeAndroidDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel
    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        dataSource = FakeAndroidDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun addReminder(){
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("Testing Reminder"), click())
        closeSoftKeyboard()

        onView(withId(R.id.reminderDescription)).perform(replaceText("Testing Desc"), click())
        closeSoftKeyboard()
        onView(withId(R.id.selectLocation)).perform(click())

        Thread.sleep(4000)

        onView(withId(R.id.map)).perform(longClick())
        onView(withId(R.id.save_btn)).perform(click())
        onView(withId(R.id.saveReminder)).perform(click())
    }

    @Test
    fun testingToast_whenNewReminderSaved(){
        val activityScenario = ActivityScenario.launch(RemindersActivity::class.java)
        dataBindingIdlingResource.monitorActivity(activityScenario)

        val reminder = ReminderDataItem("Testing Reminder","Testing Desc","location"
            ,20.0,40.0, UUID.randomUUID().toString())

        onView(withId(R.id.addReminderFAB)).perform(click())
        onView(withId(R.id.reminderTitle)).perform(replaceText("Testing Reminder"))
        onView(withId(R.id.reminderDescription)).perform(replaceText("Testing Desc"))

        saveReminderViewModel.validateAndSaveReminder(reminder)
        activityScenario.onActivity{
            onView(ViewMatchers.withText(R.string.reminder_saved))
                .inRoot(RootMatchers.withDecorView(CoreMatchers.not(it.window.decorView)))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

}