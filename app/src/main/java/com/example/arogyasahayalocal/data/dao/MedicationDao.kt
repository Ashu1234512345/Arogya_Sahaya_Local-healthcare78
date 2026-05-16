package com.example.arogyasahayalocal.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.arogyasahayalocal.model.Medication

@Dao
interface MedicationDao {
    @Query("SELECT * FROM medication_table ORDER BY alarmTime ASC")
    fun getAllMedications(): LiveData<List<Medication>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(medication: Medication)

    @Delete
    suspend fun delete(medication: Medication)
}