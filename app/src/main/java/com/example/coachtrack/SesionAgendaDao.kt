package com.example.coachtrack

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionAgendaDao {

    @Query("SELECT * FROM sesiones_agenda ORDER BY fecha, hora")
    fun getTodas(): Flow<List<SesionAgendaEntity>>

    @Query("SELECT * FROM sesiones_agenda WHERE fecha = :fecha ORDER BY hora")
    fun getPorFecha(fecha: String): Flow<List<SesionAgendaEntity>>

    // 💡 AÑADIDO: Método para obtener sesiones agendadas por el ID del alumno (Long)
    @Query("SELECT * FROM sesiones_agenda WHERE alumnoId = :alumnoId ORDER BY fecha DESC")
    fun getSesionesAgendaPorAlumno(alumnoId: Long): Flow<List<SesionAgendaEntity>>

    @Insert
    suspend fun insert(sesion: SesionAgendaEntity)

    @Update
    suspend fun update(sesion: SesionAgendaEntity)

    @Delete
    suspend fun delete(sesion: SesionAgendaEntity)

    @Query("DELETE FROM sesiones_agenda")
    suspend fun deleteAll()
}