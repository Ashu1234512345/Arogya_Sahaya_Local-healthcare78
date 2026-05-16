package com.example.arogyasahayalocal.data.entity
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile_table")
data class UserProfile(
    @PrimaryKey val id: Int = 0,
    val name: String = "",
    val age: Int = 0,
    val gender: String="",
    val bloodGroup: String = "",
    val chronicConditions: String = "",
    val emergencyName: String = "",
    val emergencyPhone: String = ""
)