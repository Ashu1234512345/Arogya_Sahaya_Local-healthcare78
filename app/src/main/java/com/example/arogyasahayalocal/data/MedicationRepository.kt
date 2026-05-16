package com.example.arogyasahayalocal.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medications")
data class MedicationEntity(
    @PrimaryKey val id: String,
    val name: String,               // Strict String representation
    val dosesPerDay: Int,           // Evaluated Int type
    val selectedDoses: List<String>,// Automatically converted structure
    val isReminderOn: Boolean,
    val reminderTime: String
)