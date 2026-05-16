package com.example.arogyasahayalocal.data.dao
import com.example.arogyasahayalocal.data.entity.HealthCamp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HealthCampDao {
   @Query("SELECT * FROM health_camps ORDER BY id DESC")
    fun getAllCamps(): Flow<List<HealthCamp>>

   @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCamp(healthCamp: HealthCamp)
}