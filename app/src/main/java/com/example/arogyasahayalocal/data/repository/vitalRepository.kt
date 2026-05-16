package com.example.arogyasahayalocal.data.repository

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import com.example.arogyasahayalocal.data.dao.VitalLogDao
import com.example.arogyasahayalocal.data.entity.VitalLog


class VitalRepository(private val vitalLogDao: VitalLogDao) {

    val latestVitals = vitalLogDao.getLatest7DaysVitals()

    suspend fun saveVital(systolic: Int, diastolic: Int, heartRate: Int) {
        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        val currentDateString = dateFormat.format(Date())
        val currentMillis = System.currentTimeMillis()
        val newLog = VitalLog(
            dateMillis = currentMillis,
            dateText = currentDateString,
            systolicBP = systolic.toFloat(),
            diastolicBP = diastolic.toFloat(),
            sugarLevel = 0f,
            temperature = 0f
        )

        vitalLogDao.insertOrUpdateVital(newLog)
    }
}