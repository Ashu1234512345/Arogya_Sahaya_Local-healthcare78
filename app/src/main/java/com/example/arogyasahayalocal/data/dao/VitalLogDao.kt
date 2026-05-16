package com.example.arogyasahayalocal.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import com.example.arogyasahayalocal.data.entity.VitalLog
@Dao
interface VitalLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateVital(vital: VitalLog)


    @Query("SELECT * FROM vital_logs ORDER BY date DESC LIMIT 7")
    fun getLatest7DaysVitals(): Flow<List<VitalLog>>
}