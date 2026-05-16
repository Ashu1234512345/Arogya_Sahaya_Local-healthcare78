package com.example.arogyasahayalocal.data.dao

import androidx.room.*
import com.example.arogyasahayalocal.data.entity.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 0")
    fun getUserProfile(): Flow<UserProfile?>

    // Fixes: Unresolved reference 'upsertProfile'
    // Upsert will Insert if new, or Update if ID 0 already exists
    @Upsert
    suspend fun upsertProfile(profile: UserProfile)

    // Fixes: Unresolved reference 'deleteProfile'
    @Query("DELETE FROM user_profile WHERE id = 0")
    suspend fun deleteProfile()
}
