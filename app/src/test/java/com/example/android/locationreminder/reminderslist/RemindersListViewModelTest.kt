package com.example.android.locationreminder.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.runner.AndroidJUnit4
import com.example.android.locationreminder.data.FakeDataStore
import com.example.android.locationreminder.locationreminders.data.dto.ReminderDTO
import com.example.android.locationreminder.locationreminders.reminderslist.RemindersListViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataStore

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        dataSource = FakeDataStore()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @Test
    fun loadReminders_WhenResultIsSuccess(){
        //Given list of reminders
        val reminders = listOf(
            ReminderDTO("title1","description1","location1",20.0,50.0),
            ReminderDTO("title2","description2","location2",30.0,40.0)
        )

        //When loadReminders return Success
        dataSource.reminders.addAll(reminders)
        viewModel.loadReminders()

        //Then the reminder list size should be 2
        assertEquals(false,viewModel.remindersList.value.isNullOrEmpty())
    }

    @Test
    fun loadReminders_WhenResultIsSuccessReminderListSizeWillBe2(){
        //Given list of  2 reminders
        val reminders = listOf(
            ReminderDTO("title1","description1","location1",20.0,50.0),
            ReminderDTO("title2","description2","location2",30.0,40.0)
        )

        //When loadReminders return Success
        dataSource.reminders.addAll(reminders)
        viewModel.loadReminders()

        //Then the reminder list size should be 2
        assertEquals(2,viewModel.remindersList.value?.size)
    }

    @Test
    fun loadReminders_WhenResultIsError(){
        val reminders = listOf(
            ReminderDTO("title1","description1","location1",20.0,50.0),
            ReminderDTO("title2","description2","location2",30.0,40.0)
        )
        dataSource.reminders.addAll(reminders)

        dataSource.setErrorState(true)
        viewModel.loadReminders()

        assertEquals("Reminders cannot be retrieved",viewModel.showSnackBar.value)
    }

    @Test
    fun invalidateShowNoData_reminderListIsEmpty(){
        //Given Empty list of reminders
        val reminders = listOf<ReminderDTO>()
        dataSource.reminders.addAll(reminders)
        viewModel.loadReminders()

        assertEquals(true,viewModel.showNoData.value)
    }

    @Test
    fun validatingSnackBarText_WhenErrorHappens(){
        //Given Error happens
        dataSource.setErrorState(true)
        //when loadReminders called from RemindersListViewModel
        viewModel.loadReminders()
        //Then showSnackBar.value should Equal "Error"
        assertEquals("Reminders cannot be retrieved",viewModel.showSnackBar.value)
    }

}