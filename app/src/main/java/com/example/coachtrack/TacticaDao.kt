package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TacticaDao {

    @Query("SELECT * FROM tacticas ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<TacticaEntity>>

    @Query("SELECT * FROM tacticas WHERE alumnoLocalId = :alumnoLocalId ORDER BY fechaCreacion DESC")
    fun getTacticasPorAlumno(alumnoLocalId: Long): Flow<List<TacticaEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(tactica: TacticaEntity)

    @Delete
    suspend fun delete(tactica: TacticaEntity)

    @Query("DELETE FROM tacticas")
    suspend fun deleteAll()
}