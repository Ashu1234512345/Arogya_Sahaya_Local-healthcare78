package com.example.arogyasahayalocal.data.entity

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "health_camps")
data class HealthCamp(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val campName: String,
    val location: String,
    val dateText: String,
    val isReminderOnly: Boolean = false, // Set to false when you are entering real camp logs!
    val patientsAttended: Int = 0        // Field to store entered camp log details
)