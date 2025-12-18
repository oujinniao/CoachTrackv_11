package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TacticaDao {

    /** Obtiene todas las tácticas (útil para la gestión global o la vista del entrenador). */
    @Query("SELECT * FROM tacticas ORDER BY fechaCreacion DESC")
    fun getAll(): Flow<List<TacticaEntity>>

    /** Obtiene las tácticas asociadas a un alumno específico por su ID local (Long). */
    @Query("SELECT * FROM tacticas WHERE alumnoLocalId = :alumnoLocalId ORDER BY fechaCreacion DESC")
    fun getTacticasPorAlumno(alumnoLocalId: Long): Flow<List<TacticaEntity>>

    /** Inserta o reemplaza una táctica. Se usa para crear nuevas o actualizar existentes. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tactica: TacticaEntity)

    /** Elimina una táctica específica. */
    @Delete
    suspend fun delete(tactica: TacticaEntity)

    /** Elimina todas las tácticas (útil para limpieza o testing). */
    @Query("DELETE FROM tacticas")
    suspend fun deleteAll()
}