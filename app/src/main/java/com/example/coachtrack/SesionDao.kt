package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow
// Usamos la lista de Kotlin explícita para evitar el error de sintaxis de Java
import kotlin.collections.List

@Dao
interface SesionDao {

    @Query("SELECT * FROM sesiones ORDER BY fecha DESC")
    fun getAll(): Flow<List<SesionEntity>>

    @Query("SELECT * FROM sesiones WHERE alumnoId = :alumnoId ORDER BY fecha DESC")
    fun getSesionesPorAlumno(alumnoId: Int): Flow<List<SesionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sesion: SesionEntity)

    @Delete
    suspend fun delete(sesion: SesionEntity)

    @Query("DELETE FROM sesiones")
    suspend fun deleteAll()
}