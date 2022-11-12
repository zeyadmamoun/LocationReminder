package com.example.android.locationreminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.android.locationreminder.data.FakeAndroidDataSource
import com.example.android.locationreminder.locationreminders.RemindersActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class RemindersActivityTest {

    private lateinit var dataSource: FakeAndroidDataSource
    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        dataSource = FakeAndroidDataSource()
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
        onView(withId(R.id.addReminderBtn)).perform(click())
    }

}