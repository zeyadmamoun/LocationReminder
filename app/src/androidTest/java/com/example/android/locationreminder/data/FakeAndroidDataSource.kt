package com.example.android.locationreminder.data

import com.example.android.locationreminder.locationreminders.data.ReminderDataSource
import com.example.android.locationreminder.locationreminders.data.dto.ReminderDTO
import com.example.android.locationreminder.locationreminders.data.dto.Result

class FakeAndroidDataSource: ReminderDataSource{

    val reminders = mutableListOf<ReminderDTO>()
    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (!shouldReturnError){
            Result.Success(reminders)
        }else{
            Result.Error("Error")
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllReminders() {
        TODO("Not yet implemented")
    }

    fun setErrorState(value: Boolean){
        shouldReturnError = value
    }
}