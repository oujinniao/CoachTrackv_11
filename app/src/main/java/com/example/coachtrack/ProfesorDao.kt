package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesorDao {

    @Query("SELECT * FROM profesores ORDER BY nombre ASC")
    fun getAll(): Flow<List<ProfesorEntity>>

    // ✅ FUNCIÓN AÑADIDA: Necesaria para la prevención de duplicación (Upsert) de Profesores durante la sync
    @Query("SELECT * FROM profesores WHERE firebaseId = :firebaseId LIMIT 1")
    suspend fun getByFirebaseId(firebaseId: String): ProfesorEntity?

    // Ahora devuelve Long con el id generado por Room
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coachTrack: ProfesorEntity): Long

    @Delete
    suspend fun delete(coachTrack: ProfesorEntity)

    @Update
    suspend fun update(profesor: ProfesorEntity)

    @Query("DELETE FROM profesores")
    suspend fun deleteAll()

    @Query("SELECT id FROM profesores WHERE firebaseId = :firebaseId LIMIT 1")
    suspend fun getLocalIdByFirebaseId(firebaseId: String): Long?
}