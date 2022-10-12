package com.example.android.locationreminder.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.example.android.locationreminder.locationreminders.data.dto.ReminderDTO
import com.example.android.locationreminder.locationreminders.data.local.RemindersDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@SmallTest
class RemindersDaoTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: RemindersDatabase

    @Before
    fun initDb(){
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertReminderAndGetById() = runTest{
        //Given Reminder and insert it into the database
        val reminder = ReminderDTO("title1","description1","location1",30.0,40.0)
        database.reminderDao().saveReminder(reminder)

        //When - Get the reminder by its id
        val savedReminder = database.reminderDao().getReminderById(reminder.id)

        //Then check on returned data
        assertNotNull(savedReminder as ReminderDTO)
        assertEquals(reminder.id,savedReminder.id)
        assertEquals(reminder.description,savedReminder.description)
        assertEquals(reminder.latitude,savedReminder.latitude)
        assertEquals(reminder.longitude,savedReminder.longitude)
        assertEquals(reminder.location,savedReminder.location)
    }

}