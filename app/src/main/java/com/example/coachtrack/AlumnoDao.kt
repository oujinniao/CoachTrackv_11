package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlumnoDao {

    @Query("SELECT * FROM alumnos ORDER BY nombre ASC")
     fun getAll(): Flow<List<AlumnoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(alumno: AlumnoEntity)

    @Update
    suspend fun update(alumno: AlumnoEntity)

    @Delete
    suspend fun delete(alumno: AlumnoEntity)

    @Query("DELETE FROM alumnos")
    suspend fun deleteAll()
}
