package com.example.arogyasahayalocal.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vital_logs")
data class VitalLog(
    val dateMillis: Long,
    val dateText: String,
    val systolicBP: Float,
    val diastolicBP: Float,
    val sugarLevel: Float,
    val temperature: Float
)