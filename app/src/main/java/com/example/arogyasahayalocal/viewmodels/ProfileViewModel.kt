package com.example.arogyasahayalocal.ui.viewmodels


import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.arogyasahayalocal.ui.screens.features.ProfileData


class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("app", Context.MODE_PRIVATE)

    private val _profileState = MutableStateFlow<ProfileData?>(null)
    val profileState = _profileState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val name = prefs.getString("user_name", "") ?: ""
        val age = try {
            prefs.getInt("user_age", 0)
        } catch (e: ClassCastException) {
            val ageString = prefs.getString("user_age", "0") ?: "0"
            ageString.toIntOrNull() ?: 0
        }
        val gender = prefs.getString("user_gender", "") ?: ""

        val blood = prefs.getString("user_blood", "") ?: ""
        val conditions = prefs.getString("user_conditions", "None") ?: "None"
        val emergencyName = prefs.getString("name_emergency", "") ?: ""
        val emergencyPhone = prefs.getString("phone_emergency", "") ?: ""


        _profileState.value = ProfileData(name, age, gender, blood, conditions, emergencyName, emergencyPhone)
    }

    fun saveProfile(
        name: String,
        age: Int,
        gender: String,
        blood: String,
        conditions: String,
        emergencyName: String,
        emergencyPhone: String
    ) {
        prefs.edit().apply {
            putString("user_name", name)
            putInt("user_age", age)
            putString("user_gender", gender)
            putString("user_blood", blood)
            putString("user_conditions", conditions)
            putString("name_emergency", emergencyName)
            putString("phone_emergency", emergencyPhone)
            apply()
        }

        _profileState.value = ProfileData(
            name,
            age,
            gender,
            blood,
            conditions,
            emergencyName,
            emergencyPhone
        )
    }

    fun logout() {
        prefs.edit().apply {
            clear()
            apply()
        }
        _profileState.value = null
    }
}