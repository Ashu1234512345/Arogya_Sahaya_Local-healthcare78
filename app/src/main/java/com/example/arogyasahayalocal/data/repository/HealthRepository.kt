package com.example.arogyasahayalocal.data.repository
import com.example.arogyasahayalocal.data.entity.HealthCamp
import kotlinx.coroutines.flow.Flow
import com.example.arogyasahayalocal.data.dao.HealthCampDao
//import com.example.arogyasahayalocal.data.


class HealthRepository(private val healthCampDao: HealthCampDao) {

    val allCamps: Flow<List<HealthCamp>> = healthCampDao.getAllCamps()

    suspend fun saveCamp(camp: HealthCamp) {
        healthCampDao.insertCamp(camp)
    }
}