package com.example.android.locationreminder.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.android.locationreminder.MainCoroutineRule
import com.example.android.locationreminder.data.FakeDataStore
import com.example.android.locationreminder.locationreminders.reminderslist.ReminderDataItem
import com.example.android.locationreminder.locationreminders.savereminder.SaveReminderViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var dataSource: FakeDataStore
    private lateinit var viewModel: SaveReminderViewModel

    @Before
    fun setupViewModel(){
        dataSource = FakeDataStore()
        viewModel = SaveReminderViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @Test
    fun validateEnteredData_EnterCompleteData(){

        viewModel.onClear()
        val reminder = ReminderDataItem("title2","description2","location2",30.0,40.0)

        val result = viewModel.validateEnteredData(reminder)

        assertEquals(true,result)
    }

    @Test
    fun saveReminder_checkToastMessage(){
        viewModel.onClear()
        val reminder = ReminderDataItem("title2","description2","location2",30.0,40.0)
        viewModel.saveReminder(reminder)

        assertEquals(1,dataSource.reminders.size)
    }

}