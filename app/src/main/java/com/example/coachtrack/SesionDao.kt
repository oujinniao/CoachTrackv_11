package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionDao {

    @Query("SELECT * FROM sesiones ORDER BY fecha DESC")
    fun getAll(): Flow<List<SesionEntity>>

    @Query("SELECT * FROM sesiones WHERE alumnoId = :alumnoId ORDER BY fecha DESC")
    fun getSesionesPorAlumno(alumnoId: Long): Flow<List<SesionEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(sesion: SesionEntity)

    @Delete
    suspend fun delete(sesion: SesionEntity)

    @Query("DELETE FROM sesiones")
    suspend fun deleteAll()
}