package com.example.android.locationreminder.data

import com.example.android.locationreminder.locationreminders.data.ReminderDataSource
import com.example.android.locationreminder.locationreminders.data.dto.ReminderDTO
import com.example.android.locationreminder.locationreminders.data.dto.Result

class FakeDataStore : ReminderDataSource {

    var reminders = mutableListOf<ReminderDTO>()
    private var shouldReturnError = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (!shouldReturnError) {
            Result.Success(reminders)
        } else {
            Result.Error("Reminders cannot be retrieved")
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        var reminder = ReminderDTO("", "", "", 0.0, 0.0)
        var isReminderFound = false

        reminders.forEach { item ->
            if (item.id == id) {
                reminder = item
                isReminderFound = true
            }
        }
        return if (isReminderFound) {
            Result.Success(reminder)
        } else {
            Result.Error("Reminders cannot be retrieved")
        }
    }

    override suspend fun deleteAllReminders() {
        reminders.clear()
    }

    override fun setErrorState(value: Boolean) {
        shouldReturnError = value
    }

}