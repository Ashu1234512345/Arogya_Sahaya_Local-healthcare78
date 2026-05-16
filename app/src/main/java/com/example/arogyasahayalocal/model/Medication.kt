package com.example.arogyasahayalocal.model
import androidx.room.Entity
import androidx.room.PrimaryKey


data class Medication(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val dosesPerDay: Int,          // Restricted to 1-5 integers
    val selectedDoses: List<String>, // e.g., ["Morning", "Night"]
    val isReminderOn: Boolean = false,
    val reminderTime: String = ""   // Format HH:mm
)