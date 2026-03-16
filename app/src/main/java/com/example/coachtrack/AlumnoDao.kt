package com.example.coachtrack

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlumnoDao {

    // -----------------------------
    // LECTURAS
    // -----------------------------
    @Query("SELECT * FROM alumnos ORDER BY nombre ASC")
    fun getAll(): Flow<List<AlumnoEntity>>

    @Query("SELECT * FROM alumnos WHERE firebaseId = :firebaseId LIMIT 1")
    suspend fun getByFirebaseId(firebaseId: String): AlumnoEntity?

    @Query("SELECT * FROM alumnos WHERE localId = :id LIMIT 1")
    suspend fun getById(id: Long): AlumnoEntity?

    @Query("SELECT * FROM alumnos WHERE profesorInstructor = :profesorId")
    suspend fun getAlumnosByProfesorId(profesorId: Long): List<AlumnoEntity>

    @Query("SELECT * FROM alumnos WHERE telefono = :telefono LIMIT 1")
    suspend fun getByTelefono(telefono: String): AlumnoEntity?

    @Query("SELECT localId FROM alumnos WHERE firebaseId = :firebaseId LIMIT 1")
    suspend fun getLocalIdByFirebaseId(firebaseId: String): Long?

    // -----------------------------
    // ESCRITURAS
    // -----------------------------
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(alumno: AlumnoEntity): Long

    @Update
    suspend fun update(alumno: AlumnoEntity)

    @Transaction
    suspend fun upsert(alumno: AlumnoEntity): Long {
        return if (alumno.localId == 0L) {
            insert(alumno)
        } else {
            update(alumno)
            alumno.localId
        }
    }

    @Query("UPDATE alumnos SET firebaseId = :firebaseId WHERE localId = :localId")
    suspend fun updateFirebaseId(localId: Long, firebaseId: String)

    @Delete
    suspend fun delete(alumno: AlumnoEntity)

    @Query("DELETE FROM alumnos")
    suspend fun deleteAll()
}