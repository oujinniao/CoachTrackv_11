package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesorDao {

    @Query("SELECT * FROM profesores ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProfesorEntity>>

    // Ahora devuelve Long con el id generado por Room
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coachTrack: ProfesorEntity): Long

    @Delete
    suspend fun delete(coachTrack: ProfesorEntity)

    @Update
    suspend fun update(profesor: ProfesorEntity)

    @Query("DELETE FROM profesores")
    suspend fun deleteAll()
}