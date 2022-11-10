package com.example.android.locationreminder.savereminder

import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.android.locationreminder.R
import com.example.android.locationreminder.data.FakeAndroidDataSource
import com.example.android.locationreminder.locationreminders.RemindersActivity
import com.example.android.locationreminder.locationreminders.data.ReminderDataSource
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module


@RunWith(AndroidJUnit4::class)
@MediumTest
@ExperimentalCoroutinesApi
class SaveReminderFragmentTest {

    private lateinit var dataSource: ReminderDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setup() {
        dataSource = FakeAndroidDataSource()
        saveReminderViewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
        stopKoin()

        val myModule = module {
            single {
                saveReminderViewModel
            }
        }
        // new koin module
        startKoin {
            modules(listOf(myModule))
        }
    }

    @Test
    fun testingToast_whenNewReminderSaved(){

        val reminder = ReminderDataItem("title1","Description","Location1",20.0,40.0)
        saveReminderViewModel.saveReminder(reminder)

        val activity = launchActivity<RemindersActivity>()

        activity.onActivity {
            onView(withText(R.string.reminder_saved)).inRoot(
                withDecorView(
                    not(
                        it.window.decorView
                    )
                )
            ).check(
                matches(
                    isDisplayed()
                )
            )
        }
    }
}